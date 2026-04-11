/*
 * demo-libnice-stun.c —— 仅用 libnice 演示 STUN（收集 srflx 候选）
 *
 * 背景
 * ----
 * STUN（RFC 5389）里客户端向 STUN 服务器发送 Binding Request，服务器在 XOR-MAPPED-ADDRESS
 *（或经典 MAPPED-ADDRESS）里返回客户端在 NAT 外侧“看起来”的源地址与端口。ICE 把这种由
 * STUN 发现的映射地址称为 server reflexive candidate（类型名常写作 srflx）。
 *
 * libnice 在“候选收集”（gathering）阶段会对配置的 STUN 服务器发 Binding 请求；成功后
 * 除本地 host 候选外，会多出一条 SERVER_REFLEXIVE 候选，其 addr 即为 NAT 映射后的
 * 公网侧地址（若存在 NAT）。
 *
 * 本程序与 demo-libnice.c 的区别
 * ------------------------------
 * 不创建对端 Agent、不交换 SDP/候选、不做连通性检测；只演示：配置 STUN → gather →
 * 在 candidate-gathering-done 信号里枚举并打印本地候选，便于单独理解 STUN 在 ICE 中的角色。
 *
 * 为何必须 attach_recv + GMainLoop
 * --------------------------------
 * NiceAgent 不会自己阻塞读 socket；STUN 响应与用户数据都经同一 UDP socket 到达。
 * 文档要求：在 gather 与后续 ICE 过程中，须通过 nice_agent_attach_recv（或等价地
 * nice_agent_recv_messages 循环）让 GLib 主循环调度读事件；否则 STUN 报文无法被处理，
 * srflx 候选可能迟迟不出现或收集失败。本例 attach_recv 后即使不收发应用数据，也必须
 * 跑 g_main_loop_run 直到 gathering 完成。
 *
 * 用法：./main [STUN主机名或IP [端口]]
 *   默认：stun.l.google.com 19302（公网示例，可能被墙或限流，可换自建 STUN）
 *
 * stun-server 与主机名
 * --------------------
 * NiceAgent 的 stun-server 属性在实现里经 nice_address_set_from_string() 处理，该函数
 * 使用 AI_NUMERICHOST，只接受 IPv4/IPv6 字面量，不会解析 DNS。传入主机名时不会发 STUN。
 * 本程序在 g_object_set 前自行 getaddrinfo 解析为数字地址。
 *
 * Debian / Ubuntu（apt）安装 libnice 开发包（含头文件与 pkg-config 名 nice）：
 *   sudo apt install libnice-dev
 *
 * 编译：
 *   gcc -o main demo-libnice-stun.c $(pkg-config --cflags --libs nice)
 */

#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <string.h>

#include <glib.h>
#include <nice/address.h>
#include <nice/agent.h>
#include <nice/candidate.h>
#include <nice/debug.h>
#include <stun/debug.h>
#ifdef __has_include
#  if __has_include (<nice/version.h>)
#    include <nice/version.h>
#  endif
#endif

/*
 * libnice 只把 stun-server 当数字 IP 字符串用；已是字面量则直接返回拷贝，否则 DNS 解析。
 */
static gchar *
stun_server_numeric (const gchar *host)
{
  NiceAddress probe;

  if (nice_address_set_from_string (&probe, host))
    return g_strdup (host);

  struct addrinfo hints, *res = NULL;
  memset (&hints, 0, sizeof hints);
  hints.ai_family = AF_UNSPEC;
  hints.ai_socktype = SOCK_DGRAM;

  if (getaddrinfo (host, NULL, &hints, &res) != 0 || res == NULL)
    g_error ("无法解析 STUN 主机: %s", host);

  gchar buf[INET6_ADDRSTRLEN];
  const gchar *ok = NULL;

  if (res->ai_family == AF_INET)
    ok = inet_ntop (AF_INET,
                    &((const struct sockaddr_in *) (void *) res->ai_addr)->sin_addr,
                    buf, sizeof buf);
  else if (res->ai_family == AF_INET6)
    ok = inet_ntop (AF_INET6,
                    &((const struct sockaddr_in6 *) (void *) res->ai_addr)->sin6_addr,
                    buf, sizeof buf);

  freeaddrinfo (res);

  if (ok == NULL)
    g_error ("STUN 地址转为字符串失败");

  return g_strdup (buf);
}

/* 与 nice_candidate_type_to_string() 等价，避免旧版 libnice 未导出该符号时链接失败 */
static const char *
candidate_type_str (NiceCandidateType t)
{
  switch (t)
    {
    case NICE_CANDIDATE_TYPE_HOST:
      return "host";
    case NICE_CANDIDATE_TYPE_SERVER_REFLEXIVE:
      return "srflx";
    case NICE_CANDIDATE_TYPE_PEER_REFLEXIVE:
      return "prflx";
    case NICE_CANDIDATE_TYPE_RELAYED:
      return "relay";
    default:
      return "unknown";
    }
}

/* 主循环：驱动 socket 可读回调，libnice 的定时器与信号也在此上下文中派发 */
static GMainLoop *loop;
static NiceAgent *agent;
/* nice_agent_add_stream 返回的流 ID，后续 gather / attach_recv / get_local_candidates 都要带上 */
static guint stream_id;

/*
 * nice_agent_attach_recv 要求的接收回调。本程序只关心 STUN gather，不产生对端用户数据；
 * 正常工作时这里通常不会被调用（STUN 由 Agent 内部消化，不会交给用户回调）。
 * 若将来在同一 component 上收发 RTP 等，则应在此解析业务报文。
 */
static void
on_recv (NiceAgent *a, guint sid, guint cid, guint len, gchar *buf, gpointer ud)
{
  (void) a;
  (void) sid;
  (void) cid;
  (void) len;
  (void) buf;
  (void) ud;
}

/*
 * candidate-gathering-done：本 stream 上本地候选收集结束（含对 STUN 的重试超时策略）。
 *
 * nice_agent_get_local_candidates 返回的 GSList 由调用方负责释放；每个 NiceCandidate
 * 需用 nice_candidate_free。典型条目包括：
 *   - HOST：本机网卡绑定地址；
 *   - SERVER_REFLEXIVE：通过上述 STUN Binding 得到的映射地址（无 NAT 时可能与 host 相似或
 *     取决于实现/网络环境）。
 *
 * nice_candidate_stun_server_address（≥0.1.20）：打印 srflx 对应的 STUN 服务器地址。
 * 旧库无此符号时在下方用 NICE_CHECK_VERSION 整段关闭，其余逻辑不变。
 */
static void
on_candidate_gathering_done (NiceAgent *a, guint sid, gpointer ud)
{
  GSList *list, *it;
  /* nice_address_to_string 只写入 IP，不含端口；缓冲区至少 NICE_ADDRESS_STRING_LEN */
  gchar ip[NICE_ADDRESS_STRING_LEN];

  (void) a;
  (void) sid;
  (void) ud;

  /* component_id 与 add_stream 时的 component 数量一致，本例仅 1 个 component */
  list = nice_agent_get_local_candidates (a, stream_id, 1);
  for (it = list; it; it = it->next)
    {
      NiceCandidate *c = it->data;
      nice_address_to_string (&c->addr, ip);
      printf ("%s\t%s:%u\n",
              candidate_type_str (c->type),
              ip,
              nice_address_get_port (&c->addr));
#ifdef NICE_CHECK_VERSION
#  if NICE_CHECK_VERSION (0, 1, 20)
      if (c->type == NICE_CANDIDATE_TYPE_SERVER_REFLEXIVE)
        {
          NiceAddress stun_addr;
          if (nice_candidate_stun_server_address (c, &stun_addr))
            {
              nice_address_to_string (&stun_addr, ip);
              printf ("  (STUN 服务器 %s:%u)\n",
                      ip, nice_address_get_port (&stun_addr));
            }
        }
#  endif
#endif
    }
  g_slist_free_full (list, (GDestroyNotify) nice_candidate_free);

  g_main_loop_quit (loop);
}

int
main (int argc, char **argv)
{
  GMainContext *ctx;
  const char *stun_host = "stun.l.google.com";
  guint stun_port = 19302;
  gchar *stun_server_ip;

  /*
   * 调试分两层：① ICE 主逻辑用 g_debug（域 libnice 等），需 G_MESSAGES_DEBUG + nice_debug_enable；
   * ② STUN 报文栈单独用 stun_debug()，必须再调用 stun_debug_enable()（见 stun/debug.h），
   *    与 nice_debug_enable 的 “stun” 标志不是一回事。NICE_DEBUG 仍影响 nice 侧子开关。
   */
//   if (g_getenv ("G_MESSAGES_DEBUG") == NULL)
//     g_setenv ("G_MESSAGES_DEBUG", "all", TRUE);
//   if (g_getenv ("NICE_DEBUG") == NULL)
//     g_setenv ("NICE_DEBUG", "stun,nice", TRUE);
//   nice_debug_enable (TRUE);
//   stun_debug_enable ();

  if (argc >= 2)
    stun_host = argv[1];
  if (argc >= 3)
    stun_port = (guint) strtoul (argv[2], NULL, 10);

  stun_server_ip = stun_server_numeric (stun_host);

  /* 默认 NULL：使用线程默认主上下文，与 NiceAgent 构造时传入的 ctx 一致 */
  loop = g_main_loop_new (NULL, FALSE);
  ctx = g_main_loop_get_context (loop);

  /*
   * NICE_COMPATIBILITY_RFC5245：标准 ICE 行为；若与对端联调需双方一致。
   * stun-server 须为数字 IP（见 stun_server_numeric）；端口默认常为 3478，Google 示例用 19302。
   */
  agent = nice_agent_new (ctx, NICE_COMPATIBILITY_RFC5245);
  g_object_set (agent,
                "stun-server", stun_server_ip,
                "stun-server-port", stun_port,
                NULL);

  /* 参数为 component 个数；返回非 0 的 stream_id 表示成功 */
  stream_id = nice_agent_add_stream (agent, 1);
  if (stream_id == 0)
    g_error ("nice_agent_add_stream failed");

  g_signal_connect (agent, "candidate-gathering-done",
                    G_CALLBACK (on_candidate_gathering_done), NULL);

  /*
   * 必须在 gather 之前 attach_recv，以便在 ctx 上注册 socket 源；
   * 否则无法接收 STUN 响应，候选收集无法正常完成。
   */
  nice_agent_attach_recv (agent, stream_id, 1, ctx, on_recv, NULL);

  /* 异步开始收集；完成后发射 candidate-gathering-done */
  if (!nice_agent_gather_candidates (agent, stream_id))
    g_error ("nice_agent_gather_candidates failed");

  g_main_loop_run (loop);

  g_object_unref (agent);
  g_free (stun_server_ip);
  g_main_loop_unref (loop);
  return EXIT_SUCCESS;
}
