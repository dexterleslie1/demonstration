/*
 * demo-libnice-coturn-only.c —— 仅配置 TURN（coturn）、无 STUN、无外部信令的 libnice 双端示例
 *
 * 设计要点
 * --------
 * - 不设置 NiceAgent 的 stun-server / stun-server-port，不向 coturn 发 STUN Binding，因而不产生
 *   libnice 的 SERVER_REFLEXIVE（srflx）候选（仍可能有本机 host 候选，由操作系统网卡决定）。
 * - 不通过 WebSocket/HTTP 等信令通道交换 SDP：在同一进程内创建两个 NiceAgent，候选收集结束后
 *   在内存中互相 nice_agent_parse_remote_sdp()，仅用于演示「除 ICE 自带信令外无单独信令服务」。
 * - 仅通过 nice_agent_set_relay_info(..., NICE_RELAY_TYPE_TURN_UDP) 指向 coturn，由 Allocate
 *   获得 RELAY（relay）候选；用户数据是否最终走中继取决于 ICE 选中的候选对（见 new-selected-pair）。
 *
 * 先启动 coturn（demo-coturn/docker-compose.yaml），默认 demo/demopass、3478。
 *
 * 用法：
 *   ./demo-libnice-coturn-only [TURN主机或IP [TURN端口]]
 * 默认 127.0.0.1:3478。主机名会解析为数字 IP 传给 libnice。
 *
 * 编译：
 *   gcc -o demo-libnice-coturn-only demo-libnice-coturn-only.c \
 *       $(pkg-config --cflags --libs nice)
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <arpa/inet.h>
#include <netdb.h>

#include <glib.h>
#include <nice/address.h>
#include <nice/agent.h>
#include <nice/candidate.h>

#define N_AGENTS               2
#define COMPONENT_ID         1
#define STREAM_NAME            "coturn"
#define DEFAULT_TURN_PORT      3478
#define DEFAULT_TURN_USER      "demo"
#define DEFAULT_TURN_PASS      "demopass"
#define RAND_STR_MIN_LEN       12
#define RAND_STR_MAX_LEN       40

static gchar *g_turn_ip_numeric;
static guint g_turn_port = DEFAULT_TURN_PORT;

static GMainLoop *loop;
static GMainContext *ctx;

static NiceAgent *g_agents[N_AGENTS];
static guint g_stream_id[N_AGENTS];
static gchar *g_sdp_local[N_AGENTS];
static gboolean g_gather_done[N_AGENTS];
static gboolean g_sdp_applied;
static int g_ready_count;
static gboolean g_timer_started;

/* 将 TURN 服务器主机名解析为数字 IP，供 nice_agent_set_relay_info 使用。 */
static gchar *
turn_server_numeric (const gchar *host)
{
  NiceAddress probe;

  if (nice_address_set_from_string (&probe, host))
    return g_strdup (host);

  struct addrinfo hints, *res = NULL;
  memset (&hints, 0, sizeof hints);
  hints.ai_family = AF_UNSPEC;
  hints.ai_socktype = SOCK_DGRAM;

  if (getaddrinfo (host, NULL, &hints, &res) != 0 || res == NULL)
    g_error ("无法解析 TURN 主机: %s", host);

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
    g_error ("地址转为字符串失败");

  return g_strdup (buf);
}

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

/*
 * 只登记 TURN（不配置 stun-server）。须在 add_stream 之后、gather 之前调用。
 */
static void
configure_turn_only (NiceAgent *a, guint stream_id)
{
  if (!nice_agent_set_relay_info (a, stream_id, COMPONENT_ID,
                                  g_turn_ip_numeric, g_turn_port,
                                  DEFAULT_TURN_USER, DEFAULT_TURN_PASS,
                                  NICE_RELAY_TYPE_TURN_UDP))
    g_error ("nice_agent_set_relay_info 失败（确认 coturn 已监听 %s:%u）",
            g_turn_ip_numeric, g_turn_port);
}

/*
 * 两个 agent 的 candidate-gathering-done 均触发后，在进程内交换 SDP，等价于「无独立信令服务器」
 * 的极简信令面；生产环境应通过安全通道交换 SDP/Trickle ICE。
 */
static void
maybe_apply_remote_sdp (void)
{
  gint n0, n1;

  if (g_sdp_applied)
    return;
  if (!g_gather_done[0] || !g_gather_done[1])
    return;
  if (g_sdp_local[0] == NULL || g_sdp_local[1] == NULL)
    return;

  n0 = nice_agent_parse_remote_sdp (g_agents[0], g_sdp_local[1]);
  n1 = nice_agent_parse_remote_sdp (g_agents[1], g_sdp_local[0]);
  if (n0 < 0 || n1 < 0)
    g_warning ("parse_remote_sdp: n0=%d n1=%d", n0, n1);

  g_sdp_applied = TRUE;
}

static void
on_candidate_gathering_done (NiceAgent *a, guint sid, gpointer user_data)
{
  gint idx = GPOINTER_TO_INT (user_data);
  gchar *sdp;

  (void) a;

  if ((guint) sid != g_stream_id[idx])
    return;

  sdp = nice_agent_generate_local_sdp (a);
  if (sdp == NULL || sdp[0] == '\0')
    g_error ("nice_agent_generate_local_sdp failed (agent %d)", idx);

  g_free (g_sdp_local[idx]);
  g_sdp_local[idx] = sdp;
  g_gather_done[idx] = TRUE;

  fprintf (stderr, "[gather-done] agent %d SDP 已生成\n", idx);

  maybe_apply_remote_sdp ();
}

static gboolean
tick_send (gpointer user_data)
{
  gchar buf[RAND_STR_MAX_LEN];
  guint n;
  static const char alphabet[] =
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  guint i;

  (void) user_data;

  n = (guint) g_random_int_range (RAND_STR_MIN_LEN, RAND_STR_MAX_LEN + 1);
  for (i = 0; i < n; i++)
    buf[i] = alphabet[g_random_int_range (0, (gint) sizeof alphabet - 1)];

  /* 仅由 controlling 端（agent 0）发送，对端在 on_recv 打印。 */
  if (nice_agent_send (g_agents[0], g_stream_id[0], COMPONENT_ID, (gint) n, buf) < 0)
    g_warning ("nice_agent_send failed");
  else
    fprintf (stderr, "[send agent 0] %u bytes\n", n);

  return G_SOURCE_CONTINUE;
}

static void
on_component_state_changed (NiceAgent *a, guint sid, guint component_id,
                            guint state, gpointer user_data)
{
  gint idx = GPOINTER_TO_INT (user_data);

  (void) a;

  if (component_id != COMPONENT_ID)
    return;
  if ((guint) sid != g_stream_id[idx])
    return;
  if (state != NICE_COMPONENT_STATE_READY)
    return;

  g_ready_count++;
  if (g_ready_count >= N_AGENTS && !g_timer_started)
    {
      g_timer_started = TRUE;
      fprintf (stderr, "双方 component READY，开始每 2 秒从 agent 0 发送数据\n");
      g_timeout_add_seconds (2, tick_send, NULL);
    }
}

static void
on_recv (NiceAgent *a, guint sid, guint cid, guint len, gchar *buf,
         gpointer user_data)
{
  gint idx = GPOINTER_TO_INT (user_data);

  (void) a;
  (void) sid;
  (void) cid;

  fprintf (stderr, "[recv agent %d] %u bytes: %.*s\n", idx, len, (int) len, buf);
}

static void
on_new_selected_pair (NiceAgent *a, guint sid, guint cid,
                      NiceCandidate *local, NiceCandidate *remote,
                      gpointer user_data)
{
  gchar la[NICE_ADDRESS_STRING_LEN], ra[NICE_ADDRESS_STRING_LEN];
  gint idx = GPOINTER_TO_INT (user_data);

  (void) a;

  if (cid != COMPONENT_ID)
    return;
  if ((guint) sid != g_stream_id[idx])
    return;

  nice_address_to_string (&local->addr, la);
  nice_address_to_string (&remote->addr, ra);

  fprintf (stderr,
           "[selected-pair agent %d] local=%s/%s:%u remote=%s/%s:%u\n",
           idx,
           candidate_type_str (local->type), la,
           nice_address_get_port (&local->addr),
           candidate_type_str (remote->type), ra,
           nice_address_get_port (&remote->addr));
}

static void
setup_one_agent (gint idx, gboolean controlling)
{
  NiceAgent *a;
  guint sid;

  a = nice_agent_new (ctx, NICE_COMPATIBILITY_RFC5245);
  g_object_set (a, "controlling-mode", controlling, NULL);

  sid = nice_agent_add_stream (a, 1);
  if (sid == 0)
    g_error ("nice_agent_add_stream failed (agent %d)", idx);
  g_stream_id[idx] = sid;

  if (!nice_agent_set_stream_name (a, sid, STREAM_NAME))
    g_error ("nice_agent_set_stream_name failed");

  configure_turn_only (a, sid);

  g_signal_connect (a, "candidate-gathering-done",
                    G_CALLBACK (on_candidate_gathering_done), GINT_TO_POINTER (idx));
  g_signal_connect (a, "component-state-changed",
                    G_CALLBACK (on_component_state_changed), GINT_TO_POINTER (idx));
  g_signal_connect (a, "new-selected-pair",
                    G_CALLBACK (on_new_selected_pair), GINT_TO_POINTER (idx));

  nice_agent_attach_recv (a, sid, COMPONENT_ID, ctx, on_recv,
                          GINT_TO_POINTER (idx));

  if (!nice_agent_gather_candidates (a, sid))
    g_error ("nice_agent_gather_candidates failed (agent %d)", idx);

  g_agents[idx] = a;
}

int
main (int argc, char **argv)
{
  const gchar *turn_host = "127.0.0.1";
  gint i;

  if (argc >= 2)
    turn_host = argv[1];
  if (argc >= 3)
    g_turn_port = (guint) strtoul (argv[2], NULL, 10);

  g_turn_ip_numeric = turn_server_numeric (turn_host);

  loop = g_main_loop_new (NULL, FALSE);
  ctx = g_main_loop_get_context (loop);

  fprintf (stderr,
           "双 NiceAgent，仅 TURN %s:%u（用户 %s）；无 STUN 属性、无 WebSocket 信令\n",
           g_turn_ip_numeric, g_turn_port, DEFAULT_TURN_USER);

  setup_one_agent (0, TRUE);
  setup_one_agent (1, FALSE);

  g_main_loop_run (loop);

  for (i = 0; i < N_AGENTS; i++)
    {
      g_clear_pointer (&g_agents[i], g_object_unref);
      g_free (g_sdp_local[i]);
    }
  g_free (g_turn_ip_numeric);
  g_main_loop_unref (loop);

  return EXIT_SUCCESS;
}
