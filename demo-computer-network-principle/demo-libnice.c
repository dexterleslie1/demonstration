/*
 * demo-libnice.c —— libnice（ICE）用法演示
 *
 * 背景简述：
 *   ICE（Interactive Connectivity Establishment）用于在 NAT/防火墙两侧找到可通的
 *   UDP 路径。libnice 实现了 ICE 代理（NiceAgent），负责收集本地候选（host/srflx/
 *   relay 等）、与对端交换候选与凭证、做连通性检测并选出可用地址对。
 *
 * 本示例特点：
 *   在同一进程里创建两个 NiceAgent，模拟通信两端，无需网络粘贴 SDP。真实场景中
 *   两端通常在不同机器，需通过信令通道交换 ufrag/pwd 与候选列表（参见 libnice
 *   examples/simple-example.c）。
 *
 * 编译：
 *   gcc -o demo-libnice demo-libnice.c $(pkg-config --cflags --libs nice)
 *
 * Windows：建议在 main 开头调用 g_networking_init()，并确保链接 gio（pkg-config
 *   一般会带上）。
 */

#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <glib.h>
#include <nice/agent.h>

/* GLib 主循环：libnice 的定时器、IO 与信号回调都依赖它 */
static GMainLoop *loop;
/* 两个 ICE 端点：A 为 controlling，B 为 controlled（决定候选优先级与提名规则） */
static NiceAgent *agent_a;
static NiceAgent *agent_b;
/* 每个 Agent 一条流，本例仅 1 个 component（一条 UDP 数据通道） */
static guint stream_a;
static guint stream_b;
/* 各自是否已收到 candidate-gathering-done（本地候选收集结束） */
static gboolean gathered_a;
static gboolean gathered_b;
/* 防止在 g_idle_add 中重复执行交换逻辑 */
static gboolean exchanged;
/* controlling 端仅在 READY 后发送一次用户数据，避免重复 send */
static gboolean sent_msg;

/*
 * 深拷贝候选链表。set_remote_candidates 不会接管列表所有权（transfer none），
 * 调用方需在设置后自行 g_slist_free_full；对端候选必须以副本形式交给另一 Agent。
 */
static GSList *
copy_candidates (const GSList *src)
{
  GSList *dst = NULL;
  const GSList *it;

  for (it = src; it; it = it->next)
    dst = g_slist_append (dst,
                          nice_candidate_copy ((NiceCandidate *) it->data));
  return dst;
}

/*
 * 在两边都 gather 完成后，交换 ICE 用户名片段（ufrag/pwd）与候选。
 *
 * - ufrag/pwd：STUN/ICE 鉴权用，必须与对端设置的“远端凭证”一致。
 * - 候选：每端用 get_local_candidates 取出后复制给对方 set_remote_candidates。
 * - 使用 g_idle_add 调度到此函数，避免在 signal 回调栈内做复杂交换，减少重入问题。
 *
 * 返回值 G_SOURCE_REMOVE：idle 源只执行一次后自动移除。
 */
static gboolean
exchange_remote_info (gpointer user_data)
{
  gchar *ua, *pa, *ub, *pb;
  GSList *la, *lb, *to_b, *to_a;

  (void) user_data;

  /* 仅当两端 gather 都完成且尚未交换过时才执行 */
  if (!gathered_a || !gathered_b || exchanged)
    return G_SOURCE_REMOVE;

  exchanged = TRUE;

  /* 取出各自本地 ICE 用户名/密码，并交叉设为对方的 remote credentials */
  if (!nice_agent_get_local_credentials (agent_a, stream_a, &ua, &pa)
      || !nice_agent_get_local_credentials (agent_b, stream_b, &ub, &pb))
    g_error ("nice_agent_get_local_credentials failed");

  nice_agent_set_remote_credentials (agent_b, stream_b, ua, pa);
  nice_agent_set_remote_credentials (agent_a, stream_a, ub, pb);
  g_free (ua);
  g_free (pa);
  g_free (ub);
  g_free (pb);

  /* get_local_candidates 返回的列表由调用者负责释放 */
  la = nice_agent_get_local_candidates (agent_a, stream_a, 1);
  lb = nice_agent_get_local_candidates (agent_b, stream_b, 1);
  to_b = copy_candidates (la);
  to_a = copy_candidates (lb);
  g_slist_free_full (la, (GDestroyNotify) nice_candidate_free);
  g_slist_free_full (lb, (GDestroyNotify) nice_candidate_free);

  /* 返回值 < 1 表示未成功加入候选（出错或列表为空） */
  if (nice_agent_set_remote_candidates (agent_b, stream_b, 1, to_b) < 1)
    g_warning ("agent_b: set_remote_candidates failed");
  g_slist_free_full (to_b, (GDestroyNotify) nice_candidate_free);

  if (nice_agent_set_remote_candidates (agent_a, stream_a, 1, to_a) < 1)
    g_warning ("agent_a: set_remote_candidates failed");
  g_slist_free_full (to_a, (GDestroyNotify) nice_candidate_free);

  return G_SOURCE_REMOVE;
}

/*
 * candidate-gathering-done：本地候选（含绑定端口、STUN 反射地址等）收集结束。
 * 两端都完成后，通过 g_idle_add 触发 exchange_remote_info。
 */
static void
on_candidate_gathering_done (NiceAgent *agent, guint stream_id,
                             gpointer user_data)
{
  (void) stream_id;
  (void) user_data;

  if (agent == agent_a)
    gathered_a = TRUE;
  else
    gathered_b = TRUE;

  g_idle_add (exchange_remote_info, NULL);
}

/*
 * component-state-changed：ICE 组件状态迁移（gathering → connecting → connected →
 * ready 等）。文档要求用户数据发送时组件宜处于 NICE_COMPONENT_STATE_READY。
 * 本例仅在 controlling 端（agent_a）首次进入 READY 时发送 7 字节 "libnice"。
 */
static void
on_component_state_changed (NiceAgent *agent, guint stream_id,
                            guint component_id, guint state,
                            gpointer user_data)
{
  (void) user_data;

  if (agent == agent_a && state == NICE_COMPONENT_STATE_READY && !sent_msg)
    {
      sent_msg = TRUE;
      if (nice_agent_send (agent, stream_id, component_id, 7, "libnice") < 0)
        g_warning ("nice_agent_send failed");
    }
}

/*
 * nice_agent_attach_recv 注册的回调：收到对端用户数据时调用（STUN 控制面数据
 * 由 libnice 内部处理，不会交给此回调）。收到后打印并结束主循环。
 */
static void
on_recv (NiceAgent *agent, guint stream_id, guint component_id,
         guint len, gchar *buf, gpointer user_data)
{
  (void) agent;
  (void) stream_id;
  (void) component_id;
  (void) user_data;

  printf ("收到 UDP 数据 (%u 字节): %.*s\n", len, (int) len, buf);
  g_main_loop_quit (loop);
}

int
main (void)
{
  GMainContext *ctx;

  /* 默认主上下文：与 NiceAgent、attach_recv 使用同一 ctx，保证回调在同一线程派发 */
  loop = g_main_loop_new (NULL, FALSE);
  ctx = g_main_loop_get_context (loop);

  /* RFC5245 兼容模式；两端须使用一致的 compat */
  agent_a = nice_agent_new (ctx, NICE_COMPATIBILITY_RFC5245);
  agent_b = nice_agent_new (ctx, NICE_COMPATIBILITY_RFC5245);
  g_object_set (agent_a, "controlling-mode", TRUE, NULL);
  g_object_set (agent_b, "controlling-mode", FALSE, NULL);

  /* 各增加 1 个 component；返回值为 stream_id，失败时为 0 */
  stream_a = nice_agent_add_stream (agent_a, 1);
  stream_b = nice_agent_add_stream (agent_b, 1);
  if (stream_a == 0 || stream_b == 0)
    g_error ("nice_agent_add_stream failed");

  g_signal_connect (agent_a, "candidate-gathering-done",
                    G_CALLBACK (on_candidate_gathering_done), NULL);
  g_signal_connect (agent_b, "candidate-gathering-done",
                    G_CALLBACK (on_candidate_gathering_done), NULL);
  g_signal_connect (agent_a, "component-state-changed",
                    G_CALLBACK (on_component_state_changed), NULL);

  /*
   * 必须在 gather 之前 attach_recv：否则无法在本 context 上接收 STUN/数据报，
   * 候选收集也无法正常完成。
   */
  nice_agent_attach_recv (agent_a, stream_a, 1, ctx, on_recv, NULL);
  nice_agent_attach_recv (agent_b, stream_b, 1, ctx, on_recv, NULL);

  /* 异步开始收集；完成后会发 candidate-gathering-done */
  if (!nice_agent_gather_candidates (agent_a, stream_a)
      || !nice_agent_gather_candidates (agent_b, stream_b))
    g_error ("nice_agent_gather_candidates failed");

  g_main_loop_run (loop);

  g_object_unref (agent_a);
  g_object_unref (agent_b);
  g_main_loop_unref (loop);
  return EXIT_SUCCESS;
}
