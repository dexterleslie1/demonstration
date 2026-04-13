/*
 * demo-libnice-exchange-sdp.c —— 两进程通过 Node 信令服（demo-signal-server.js）交换 ICE SDP
 *
 * 与 demo-libnice-sdp.c 一致，仅走 relay 信令面：
 *   - nice_agent_set_relay_info() 向 coturn 申请 relay 候选；
 *   - nice_agent_generate_local_sdp() 后对 SDP 文本删除 typ host、typ srflx 的 a=candidate 行，
 *     再打印与经 WebSocket 发送；对端 parse_remote_sdp 只导入 relay（及 prflx 若出现）。
 * 须先启动 demo-coturn（账号与 TURN_SERVER_* 宏一致）。
 * Docker 部署时若 relay 候选里是 172.x 等容器地址、跨机无法打 UDP，请在 coturn 上配置
 * --external-ip=<对端可达的局域网或公网 IP>（见 demo-coturn/docker-compose.yaml 注释）。
 *
 * 信令服行为（见 demo-signal-server.js）：连到 ws://<host>:<port>/connector 的客户端之间，
 * 某一端发出的文本帧会广播给其他客户端（发送者本人不收）。两机各跑一进程时，等价于互发 SDP。
 *
 * SDP 交换：
 *   - answer：候选收集结束且 WebSocket 已连上即发本地 SDP（connect 或 gather 回调中补发）。
 *   - offer：收到对端 SDP 后若本地 SDP 已就绪则立即回发；若 answer 因 TURN 等更快 gather 完成、
 *     SDP 先到而 offer 尚未生成本地 SDP，则在 offer 的 candidate-gathering-done 里检测到已有
 *     peer_sdp 时再补发（与 answer 侧「gather 后补发」对称），避免 answer 永远收不到 offer。
 * 仍建议先起 offer 再起 answer，但双端 SDP 在任意 gather 顺序下都应能凑齐。
 *
 * 用法：
 *   1) 启动信令：在 demo-libnice 目录 node demo-signal-server.js（默认 8765）
 *   2) ./main offer [WebSocket URL]
 *   3) ./main answer [WebSocket URL]
 * 默认 URL：ws://127.0.0.1:8765/connector
 *
 * 编译（在含本文件的目录下）：
 *   gcc -o main demo-libnice-exchange-sdp.c $(pkg-config --cflags --libs nice libsoup-2.4)
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>
#include <arpa/inet.h>
#include <netdb.h>

#include <glib.h>
#include <nice/address.h>
#include <nice/agent.h>
#include <nice/candidate.h>
#include <libsoup/soup.h>

#define COMPONENT_ID 1
/* libnice 要求与 RFC 一致：m= 媒体类型须为 audio/video 等，否则 CRITICAL 且对端解析易异常 */
#define STREAM_NAME "audio"
#define DEFAULT_SIGNAL_URL "ws://192.168.1.181:8765/connector"

/* 与 demo-libnice-sdp.c、demo-coturn/docker-compose.yaml 中 lt-cred-mech、listening-port 一致 */
#define TURN_SERVER_IP         "192.168.1.28"
#define TURN_SERVER_PORT       3478
#define TURN_USER              "demo"
#define TURN_PASS              "demopass"

#define RAND_STR_MIN_LEN 12
#define RAND_STR_MAX_LEN 40

static GMainLoop *loop;
static NiceAgent *agent;
static guint stream_id;
static gboolean timer_started;

static SoupSession *ws_session;
static SoupWebsocketConnection *ws_conn;
static gchar *local_sdp;
static gchar *peer_sdp;
static gboolean peer_applied;

static void maybe_apply_peer (void);

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

/* 日志行前缀：yyyy-MM-dd HH:mm:ss.SSS（本地时区） */
static void
log_ts_prefix (FILE *fp)
{
  gint64 now_us;
  GDateTime *dt;
  gchar *s;
  gint ms;

  now_us = g_get_real_time ();
  dt = g_date_time_new_from_unix_local (now_us / G_USEC_PER_SEC);
  ms = (gint) ((now_us % G_USEC_PER_SEC) / 1000);
  s = g_date_time_format (dt, "%Y-%m-%d %H:%M:%S");
  fprintf (fp, "%s.%03d ", s, ms);
  g_free (s);
  g_date_time_unref (dt);
}

static void
log_msg (FILE *fp, const char *fmt, ...)
{
  va_list ap;

  log_ts_prefix (fp);
  va_start (ap, fmt);
  vfprintf (fp, fmt, ap);
  va_end (ap);
}

static void
log_warn (const char *fmt, ...)
{
  va_list ap;

  log_ts_prefix (stderr);
  fputs ("[WARN] ", stderr);
  va_start (ap, fmt);
  vfprintf (stderr, fmt, ap);
  va_end (ap);
  fputc ('\n', stderr);
}

static void G_GNUC_NORETURN
log_fatal (const char *fmt, ...)
{
  va_list ap;

  log_ts_prefix (stderr);
  va_start (ap, fmt);
  vfprintf (stderr, fmt, ap);
  va_end (ap);
  fputc ('\n', stderr);
  fflush (stderr);
  abort ();
}

/*
 * 从 SDP 中移除 typ host、typ srflx 的 a=candidate 行（与 demo-libnice-sdp.c 相同），
 * 信令上只保留 relay 等，强制对端仅见 relay 候选。
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

      // 删除host和srflx候选
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

static void
ws_send_text (const gchar *text)
{
  if (ws_conn == NULL)
    return;
  soup_websocket_connection_send_text (ws_conn, text);
}

static void
ws_on_message (SoupWebsocketConnection *conn,
               SoupWebsocketDataType type,
               GBytes *message,
               gpointer user_data)
{
  gsize len;
  const gchar *data;

  (void) user_data;

  if (type != SOUP_WEBSOCKET_DATA_TEXT)
    return;

  data = g_bytes_get_data (message, &len);
  g_free (peer_sdp);
  peer_sdp = g_strndup (data, len);
  log_msg (stderr, "[信令] 收到对端 SDP，%zu 字节\n", len);

  /* 协议：answer 先发；offer 收到后再回发（须本地 SDP 已生成） */
  if (g_object_get_data (G_OBJECT (conn), "is-offer") && local_sdp != NULL)
    {
      log_msg (stderr, "[信令] offer 回发本地 SDP\n");
      ws_send_text (local_sdp);
    }

  maybe_apply_peer ();
}

static void
ws_on_closed (SoupWebsocketConnection *conn, gpointer user_data)
{
  (void) user_data;

  if (ws_conn == conn)
    ws_conn = NULL;
}

static void
maybe_apply_peer (void)
{
  gint n;

  if (peer_applied)
    return;
  if (local_sdp == NULL || peer_sdp == NULL)
    return;

  n = nice_agent_parse_remote_sdp (agent, peer_sdp);
  if (n < 0)
    log_warn ("nice_agent_parse_remote_sdp 失败");
  else
    {
      peer_applied = TRUE;
      log_msg (stderr, "[ICE] 已解析对端 SDP\n");
    }
}

static void
ws_connected (GObject *source, GAsyncResult *res, gpointer user_data)
{
  gboolean is_offer = GPOINTER_TO_INT (user_data);
  GError *err = NULL;
  SoupSession *session = SOUP_SESSION (source);

  ws_conn = soup_session_websocket_connect_finish (session, res, &err);
  if (!ws_conn)
    log_fatal ("WebSocket 连接失败: %s", err->message);

  g_object_set_data (G_OBJECT (ws_conn), "is-offer",
                     GINT_TO_POINTER (is_offer ? 1 : 0));
  g_signal_connect (ws_conn, "message", G_CALLBACK (ws_on_message), NULL);
  g_signal_connect (ws_conn, "closed", G_CALLBACK (ws_on_closed), NULL);

  log_msg (stderr, "[信令] 已连接 %s\n", is_offer ? "(offer)" : "(answer)");

  /* answer：已连上且本地 SDP 已就绪则立即发送（与 p2p 一致） */
  if (!is_offer && local_sdp != NULL)
    {
      log_msg (stderr, "[信令] answer 发送本地 SDP\n");
      ws_send_text (local_sdp);
    }
}

static void
random_payload (gchar *buf, guint len)
{
  static const char alphabet[] =
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  guint i;

  for (i = 0; i < len; i++)
    buf[i] = alphabet[g_random_int_range (0, (gint) sizeof alphabet - 1)];
}

static gboolean
tick_send (gpointer user_data)
{
  gchar buf[RAND_STR_MAX_LEN];
  guint n;

  (void) user_data;

  n = (guint) g_random_int_range (RAND_STR_MIN_LEN, RAND_STR_MAX_LEN + 1);
  random_payload (buf, n);

  if (nice_agent_send (agent, stream_id, COMPONENT_ID, (gint) n, buf) < 0)
    log_warn ("nice_agent_send 失败");
  else
    log_msg (stderr, "[send] %u 字节: %.*s\n", n, (int) n, buf);

  return G_SOURCE_CONTINUE;
}

static void
on_component_state_changed (NiceAgent *a, guint sid, guint component_id,
                            guint state, gpointer user_data)
{
  (void) a;
  (void) sid;
  (void) user_data;

  if (component_id != COMPONENT_ID)
    return;
  if (state != NICE_COMPONENT_STATE_READY)
    return;
  if (timer_started)
    return;
  timer_started = TRUE;
  log_msg (stderr, "[ICE] 组件 READY，每 2 秒发送随机串（Ctrl+C 退出）\n");
  g_timeout_add_seconds (2, tick_send, NULL);
}

static void
on_recv (NiceAgent *a, guint sid, guint cid, guint len, gchar *buf,
         gpointer user_data)
{
  (void) a;
  (void) sid;
  (void) cid;
  (void) user_data;

  log_msg (stderr, "[recv] %u 字节: %.*s\n", len, (int) len, buf);
}

static void
on_candidate_gathering_done (NiceAgent *a, guint sid, gpointer user_data)
{
  gboolean is_offer = GPOINTER_TO_INT (user_data);

  (void) sid;

  g_free (local_sdp);
  {
    gchar *raw;

    raw = nice_agent_generate_local_sdp (a);
    if (raw == NULL || raw[0] == '\0')
      log_fatal ("nice_agent_generate_local_sdp 失败");
    local_sdp = sdp_drop_host_and_srflx_candidate_lines (raw);
    g_free (raw);
  }
  if (local_sdp == NULL || local_sdp[0] == '\0')
    log_fatal ("过滤 host/srflx 后 SDP 为空（请确认 coturn 已启动且 TURN_SERVER_IP 可达）");

  log_msg (stderr, "[ICE] 本地 SDP 已生成（%s，仅 relay 等候选发往信令）\n",
           is_offer ? "offer/controlling" : "answer/controlled");
  log_msg (stderr, "----- 本地 SDP -----\n");
  log_ts_prefix (stderr);
  fputs (local_sdp, stderr);
  if (local_sdp[0] != '\0' && local_sdp[strlen (local_sdp) - 1] != '\n')
    fputc ('\n', stderr);
  log_msg (stderr, "----- 结束 -----\n");

  /* answer：候选就绪后若 WebSocket 已连上则发送 */
  if (!is_offer && ws_conn != NULL)
    {
      log_msg (stderr, "[信令] answer 发送本地 SDP（gather 后补发）\n");
      ws_send_text (local_sdp);
    }

  /*
   * offer：若对端（answer）因 relay 等更早完成 gather 并已发来 SDP，此前 ws_on_message 里
   * local_sdp 尚为空无法回发；本地 SDP 就绪后必须补发，否则 answer 无法 parse offer，ICE 不能双向完成。
   */
  if (is_offer && peer_sdp != NULL && ws_conn != NULL)
    {
      log_msg (stderr, "[信令] offer 发送本地 SDP（gather 晚于对端，gather 后补发）\n");
      ws_send_text (local_sdp);
    }

  maybe_apply_peer ();
}

static int
run_peer (gboolean is_offer, const gchar *ws_url)
{
  GMainContext *ctx;
  SoupMessage *msg;
  // const char *stun_host = "stun.l.google.com";
  // guint stun_port = 19302;
  // gchar *stun_ip = NULL;

  loop = g_main_loop_new (NULL, FALSE);
  ctx = g_main_loop_get_context (loop);

  agent = nice_agent_new (ctx, NICE_COMPATIBILITY_RFC5245);
  g_object_set (agent, "controlling-mode", is_offer ? TRUE : FALSE, NULL);

  stream_id = nice_agent_add_stream (agent, 1);
  if (stream_id == 0)
    log_fatal ("nice_agent_add_stream 失败");
  if (!nice_agent_set_stream_name (agent, stream_id, STREAM_NAME))
    log_fatal ("nice_agent_set_stream_name 失败");

  if (!nice_agent_set_relay_info (agent, stream_id, COMPONENT_ID,
                                  TURN_SERVER_IP, TURN_SERVER_PORT,
                                  TURN_USER, TURN_PASS,
                                  NICE_RELAY_TYPE_TURN_UDP))
    log_fatal ("nice_agent_set_relay_info 失败（请先启动 demo-coturn）");

  // stun_ip = stun_server_numeric (stun_host);
  // g_object_set (agent,
  //             "stun-server", stun_ip,
  //             "stun-server-port", stun_port,
  //             NULL);

  g_signal_connect (agent, "candidate-gathering-done",
                    G_CALLBACK (on_candidate_gathering_done),
                    GINT_TO_POINTER (is_offer ? 1 : 0));
  g_signal_connect (agent, "component-state-changed",
                    G_CALLBACK (on_component_state_changed), NULL);

  nice_agent_attach_recv (agent, stream_id, COMPONENT_ID, ctx, on_recv, NULL);

  if (!nice_agent_gather_candidates (agent, stream_id))
    log_fatal ("nice_agent_gather_candidates 失败");

  ws_session = soup_session_new ();
  msg = soup_message_new ("GET", ws_url);
  soup_session_websocket_connect_async (ws_session, msg, NULL, NULL, NULL,
                                        ws_connected,
                                        GINT_TO_POINTER (is_offer ? 1 : 0));
  g_object_unref (msg);

  log_msg (stderr, "连接信令: %s （角色: %s，请先 offer 后 answer）\n",
           ws_url, is_offer ? "offer" : "answer");

  g_main_loop_run (loop);

  g_object_unref (agent);
  g_clear_object (&ws_session);
  g_clear_pointer (&ws_conn, g_object_unref);
  g_free (local_sdp);
  g_free (peer_sdp);
  // g_free (stun_ip);
  g_main_loop_unref (loop);
  return EXIT_SUCCESS;
}

int
main (int argc, char **argv)
{
  const gchar *url = DEFAULT_SIGNAL_URL;

  if (argc < 2)
    {
      log_msg (stderr,
               "用法:\n"
               "  %s offer [WebSocket URL]\n"
               "  %s answer [WebSocket URL]\n"
               "默认 URL: %s\n"
               "先启动: node demo-signal-server.js\n",
               argv[0], argv[0], DEFAULT_SIGNAL_URL);
      return EXIT_FAILURE;
    }

  if (argc >= 3)
    url = argv[2];

  if (strcmp (argv[1], "offer") == 0)
    return run_peer (TRUE, url);

  if (strcmp (argv[1], "answer") == 0)
    return run_peer (FALSE, url);

  log_msg (stderr, "第一个参数必须是 offer 或 answer\n");
  return EXIT_FAILURE;
}
