/*
 * demo-libnice-sdp.c —— A 端（offer）在候选收集完成后生成 ICE SDP
 *
 * 背景
 * ----
 * WebRTC / ICE 里「offer」一侧需要把本地 ICE 用户名片段（ice-ufrag、ice-pwd）以及
 * 各候选（candidate 行）交给对端。libnice 在候选收集结束后可用
 * nice_agent_generate_local_sdp() 一次性生成可交给 nice_agent_parse_remote_sdp() 解析的
 * SDP 文本（不含编解码 m= 里的 payload，仅 ICE 相关行）。
 *
 * 本程序演示 A 端典型设置：
 *   - controlling-mode（见下「与 SDP 的关系」）；
 *   - add_stream + set_stream_name（未命名 stream 生成的 SDP 可能无法被 parse_remote_sdp 解析）；
 *   - TURN：nice_agent_set_relay_info() 指向本机 coturn 127.0.0.1:3478（与 demo-coturn/docker-compose
 *     默认账号 demo / demopass 一致），SDP 中会出现 relay 候选；
 *   - 可选配置 STUN（命令行参数，与 demo-libnice-stun.c 相同），便于额外出现 srflx；
 *   - candidate-gathering-done 后调用 nice_agent_generate_local_sdp()，再删掉其中 typ 为
 *     host、srflx 的 a=candidate 行后打印（libnice 无单独开关，此处对 SDP 文本做后处理）。
 *     若未配置 TURN，删完后可能只剩 prflx（一般不会有）或无任何 candidate 行。
 *
 * controlling-mode 与 SDP 文本
 * ----------------------------
 * NiceAgent 的 controlling-mode 默认值为 FALSE（见官方文档 NiceAgent:controlling-mode）。
 * 它表示本端在 ICE 中是 controlling 还是 controlled，用于与对端一起做连通性检测、提名等；
 * nice_agent_generate_local_sdp() 只输出 ice-ufrag / ice-pwd / candidate 等，一般不随该标志改变，
 * 因此单独注释掉 g_object_set(controlling-mode, TRUE) 时，你看到的 SDP 往往与开启时相同。
 * 与对端联调时仍建议：offer/controlling 一侧设为 TRUE，answer/controlled 一侧设为 FALSE，
 * 与 demo-libnice-p2p.c 一致，否则可能在角色冲突或提名阶段与预期不符。
 *
 * a=candidate 行里「第一个数字」不是序号
 * ----------------------------------------
 * RFC 5245 中 candidate-attribute 格式为：foundation SP component-id SP transport SP …
 * 冒号后的第一段是 foundation（基础标识，用于把同类候选分组），由实现自行分配，常见为数字
 * 字符串但并非必须从 1 递增；第二段才是 component-id（多为 1、2…）。因此出现
 * 「a=candidate:31 1 UDP …」表示 foundation 为 "31"、component 为 1，并非「第 31 条候选」，
 * 与对端解析、连通性检测无冲突。
 *
 * 用法：./main [STUN主机或IP [STUN端口]]
 *   不传参数则不设置 stun-server；TURN 始终为 127.0.0.1:3478。
 *   若需 srflx，可传 127.0.0.1 3478 与本地 coturn 复用同一端口。
 *
 * 编译：
 *   gcc -o main demo-libnice-sdp.c $(pkg-config --cflags --libs nice)
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

#define STREAM_NAME "audio"
#define COMPONENT_ID 1

/* 与 demo-coturn/docker-compose.yaml 中 lt-cred-mech、listening-port 一致 */
#define TURN_SERVER_IP         "192.168.1.28"
#define TURN_SERVER_PORT       3478
#define TURN_USER              "demo"
#define TURN_PASS              "demopass"

static GMainLoop *loop;
static NiceAgent *agent;
static guint stream_id;

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
 * 从 SDP 文本中移除 a=candidate 行里 candidate-types 为 host、srflx 的条目（RFC 5245 行末
 * 「typ host」「typ srflx」）。不删 prflx / relay。注意：m=/c= 默认地址仍可能对应已删候选，
 * 与对端联调时需自行保证一致或仅作演示。
 */
static gchar *
sdp_drop_host_and_srflx_candidate_lines (const gchar *sdp)
{
  GString *out;
  gchar **lines;
  gint i;
  gboolean first = TRUE;

  out = g_string_new (NULL);
  lines = g_strsplit (sdp, "\n", -1);

  for (i = 0; lines[i] != NULL; i++)
    {
      gchar *check;
      gboolean drop;

      check = g_strdup (lines[i]);
      {
        gsize L = strlen (check);
        if (L > 0 && check[L - 1] == '\r')
          check[L - 1] = '\0';
      }

      drop = g_str_has_prefix (check, "a=candidate:")
             && (strstr (check, " typ host") != NULL
                 || strstr (check, " typ srflx") != NULL);
      g_free (check);

      if (drop)
        continue;

      if (!first)
        g_string_append_c (out, '\n');
      first = FALSE;
      g_string_append (out, lines[i]);
    }

  g_strfreev (lines);
  return g_string_free (out, FALSE);
}

/*
 * 候选就绪后生成「本地 SDP」——即 A 端 offer 中承载 ICE 信息的那一段文本。
 * 对端 B 收到后应 nice_agent_parse_remote_sdp() 导入，再交换 B 的 SDP / 候选。
 */
static void
on_candidate_gathering_done (NiceAgent *a, guint sid, gpointer ud)
{
  gchar *sdp;
  gchar *filtered;

  (void) sid;
  (void) ud;

  sdp = nice_agent_generate_local_sdp (a);
  if (sdp == NULL || sdp[0] == '\0')
    g_error ("nice_agent_generate_local_sdp 失败");

  // 从sdp中删除host和srflx候选
  filtered = sdp_drop_host_and_srflx_candidate_lines (sdp);
  g_free (sdp);

  fputs ("----- A 端 offer（已去掉 typ host / typ srflx 的 candidate 行）-----\n", stdout);
  fputs (filtered, stdout);
  if (filtered[0] != '\0' && filtered[strlen (filtered) - 1] != '\n')
    putchar ('\n');
  fputs ("----- 结束 -----\n", stdout);

  g_free (filtered);
  g_main_loop_quit (loop);
}

int
main (int argc, char **argv)
{
  GMainContext *ctx;
  gboolean use_stun = FALSE;
  const char *stun_host = "stun.l.google.com";
  guint stun_port = 19302;
  gchar *stun_ip = NULL;

  if (argc >= 2)
    {
      use_stun = TRUE;
      stun_host = argv[1];
    }
  if (argc >= 3)
    stun_port = (guint) strtoul (argv[2], NULL, 10);

  loop = g_main_loop_new (NULL, FALSE);
  ctx = g_main_loop_get_context (loop);

  agent = nice_agent_new (ctx, NICE_COMPATIBILITY_RFC5245);

  /*
   * A 端作为 offer：与 demo-libnice-p2p 的 offer 一致设为 controlling。
   * 若删掉本行，默认 controlling-mode=FALSE，但本程序仅打印 generate_local_sdp()，
   * 输出通常仍相同（见文件头「controlling-mode 与 SDP 文本」）。
   */
  g_object_set (agent, "controlling-mode", TRUE, NULL);

  stream_id = nice_agent_add_stream (agent, 1);
  if (stream_id == 0)
    g_error ("nice_agent_add_stream failed");
  if (!nice_agent_set_stream_name (agent, stream_id, STREAM_NAME))
    g_error ("nice_agent_set_stream_name failed");

  /*
   * TURN：向本机 coturn 申请 relay 候选（SDP 中出现 typ relay）。
   * 服务器地址须为数字 IP 形式；127.0.0.1 已满足。
   */
  if (!nice_agent_set_relay_info (agent, stream_id, COMPONENT_ID,
                                  TURN_SERVER_IP, TURN_SERVER_PORT,
                                  TURN_USER, TURN_PASS,
                                  NICE_RELAY_TYPE_TURN_UDP))
    g_error ("nice_agent_set_relay_info 失败（请先启动 demo-coturn）");

    // 不使用stun协议获取srflx候选
    // stun_ip = stun_server_numeric (stun_host);
    // g_object_set (agent,
    //             "stun-server", stun_ip,
    //             "stun-server-port", stun_port,
    //             NULL);

  g_signal_connect (agent, "candidate-gathering-done",
                    G_CALLBACK (on_candidate_gathering_done), NULL);

  nice_agent_attach_recv (agent, stream_id, COMPONENT_ID, ctx, on_recv, NULL);

  if (!nice_agent_gather_candidates (agent, stream_id))
    g_error ("nice_agent_gather_candidates failed");

  g_main_loop_run (loop);

  g_object_unref (agent);
  g_free (stun_ip);
  g_main_loop_unref (loop);
  return EXIT_SUCCESS;
}
