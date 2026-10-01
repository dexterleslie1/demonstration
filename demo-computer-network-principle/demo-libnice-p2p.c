/*
 * demo-libnice-p2p.c —— 两个独立进程用 libnice（ICE）建 P2P，再每 2 秒互发随机串
 *
 * 进程 A（offer / controlling）与进程 B（answer / controlled）通过 WebSocket 信令交换
 * ICE SDP（nice_agent_generate_local_sdp / nice_agent_parse_remote_sdp），再跑 ICE
 * 连通性检测。
 *
 * 用法（先起 offer，再起 answer）：
 *   ./demo-libnice-p2p offer [信令端口]
 *   ./demo-libnice-p2p answer <信令主机> <信令端口>
 * 默认信令端口 9999；本机双机可均用 127.0.0.1。
 *
 * 编译：
 *   gcc -o demo-libnice-p2p demo-libnice-p2p.c $(pkg-config --cflags --libs nice libsoup-2.4)
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <glib.h>
#include <nice/agent.h>
#include <libsoup/soup.h>

#define COMPONENT_ID 1
#define STREAM_NAME "p2p"
#define DEFAULT_SIG_PORT 9999
#define RAND_STR_MIN_LEN 12
#define RAND_STR_MAX_LEN 40

static GMainLoop *loop;
static NiceAgent *agent;
static guint stream_id;
static gboolean timer_started;

/* WebSocket 信令：交换 SDP 字符串（文本帧）。 */
static SoupServer *ws_server;                 /* offer only */
static SoupSession *ws_session;               /* answer only */
static SoupWebsocketConnection *ws_conn;      /* offer/answer */
static gchar *local_sdp;                      /* offer/answer */
static gchar *peer_sdp;                       /* offer/answer */
static gboolean peer_applied;

static void maybe_apply_peer (void);

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

  /* 协议：answer 先发 SDP；offer 收到后再回发自己的 SDP。 */
  if (g_object_get_data (G_OBJECT (conn), "is-offer") && local_sdp != NULL)
    ws_send_text (local_sdp);

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
    g_warning ("nice_agent_parse_remote_sdp failed");
  else
    peer_applied = TRUE;
}

static void
offer_ws_handler (SoupServer *server,
                  SoupWebsocketConnection *conn,
                  const char *path,
                  SoupClientContext *client,
                  gpointer user_data)
{
  (void) server;
  (void) path;
  (void) client;
  (void) user_data;

  ws_conn = conn;
  g_object_set_data (G_OBJECT (conn), "is-offer", GINT_TO_POINTER (TRUE));

  g_signal_connect (conn, "message", G_CALLBACK (ws_on_message), NULL);
  g_signal_connect (conn, "closed", G_CALLBACK (ws_on_closed), NULL);

  /* 等对端先发 SDP；收到后在 ws_on_message 回发 local_sdp。 */
}

static void
answer_ws_connected (GObject *source, GAsyncResult *res, gpointer user_data)
{
  GError *err = NULL;
  SoupSession *session = SOUP_SESSION (source);

  (void) user_data;

  ws_conn = soup_session_websocket_connect_finish (session, res, &err);
  if (!ws_conn)
    g_error ("websocket connect failed: %s", err->message);

  g_object_set_data (G_OBJECT (ws_conn), "is-offer", GINT_TO_POINTER (FALSE));
  g_signal_connect (ws_conn, "message", G_CALLBACK (ws_on_message), NULL);
  g_signal_connect (ws_conn, "closed", G_CALLBACK (ws_on_closed), NULL);

  if (local_sdp != NULL)
    ws_send_text (local_sdp); /* 协议：answer 先发 */
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
    g_warning ("nice_agent_send failed");
  else
    fprintf (stderr, "[send] %u bytes\n", n);

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

  fprintf (stderr, "[recv] %u bytes: %.*s\n", len, (int) len, buf);
}

static void
on_candidate_gathering_done (NiceAgent *a, guint sid, gpointer user_data)
{
  gboolean is_offer = GPOINTER_TO_INT (user_data);

  (void) sid;

  g_free (local_sdp);
  local_sdp = nice_agent_generate_local_sdp (a);
  if (!local_sdp || local_sdp[0] == '\0')
    g_error ("nice_agent_generate_local_sdp failed");

  if (!is_offer && ws_conn != NULL)
    ws_send_text (local_sdp); /* answer 可能已连上，gather 才刚结束 */

  maybe_apply_peer ();
}

static int
run_offer (guint16 port)
{
  GMainContext *ctx;
  GError *err = NULL;

  loop = g_main_loop_new (NULL, FALSE);
  ctx = g_main_loop_get_context (loop);

  ws_server = soup_server_new (NULL, NULL);
  soup_server_add_websocket_handler (ws_server, "/ws", NULL, NULL,
                                     offer_ws_handler, NULL, NULL);
  if (!soup_server_listen_all (ws_server, port, 0, &err))
    g_error ("websocket listen %u: %s", port, err->message);
  fprintf (stderr, "offer: WebSocket 信令监听 ws://0.0.0.0:%u/ws (先起本进程，再起 answer)\n",
           (unsigned) port);

  agent = nice_agent_new (ctx, NICE_COMPATIBILITY_RFC5245);
  g_object_set (agent, "controlling-mode", TRUE, NULL);

  stream_id = nice_agent_add_stream (agent, 1);
  if (stream_id == 0)
    g_error ("nice_agent_add_stream failed");
  if (!nice_agent_set_stream_name (agent, stream_id, STREAM_NAME))
    g_error ("nice_agent_set_stream_name failed");

  g_signal_connect (agent, "candidate-gathering-done",
                    G_CALLBACK (on_candidate_gathering_done), GINT_TO_POINTER (TRUE));
  g_signal_connect (agent, "component-state-changed",
                    G_CALLBACK (on_component_state_changed), NULL);

  nice_agent_attach_recv (agent, stream_id, COMPONENT_ID, ctx, on_recv, NULL);

  if (!nice_agent_gather_candidates (agent, stream_id))
    g_error ("nice_agent_gather_candidates failed");

  g_main_loop_run (loop);

  g_object_unref (agent);
  g_clear_object (&ws_server);
  g_free (local_sdp);
  g_free (peer_sdp);
  g_main_loop_unref (loop);
  return EXIT_SUCCESS;
}

static int
run_answer (const gchar *host, guint16 port)
{
  GMainContext *ctx;
  gchar *url;
  SoupMessage *msg;

  loop = g_main_loop_new (NULL, FALSE);
  ctx = g_main_loop_get_context (loop);

  agent = nice_agent_new (ctx, NICE_COMPATIBILITY_RFC5245);
  g_object_set (agent, "controlling-mode", FALSE, NULL);

  ws_session = soup_session_new ();
  url = g_strdup_printf ("ws://%s:%u/ws", host, (unsigned) port);
  msg = soup_message_new ("GET", url);
  g_free (url);
  soup_session_websocket_connect_async (ws_session, msg, NULL, NULL, NULL,
                                        answer_ws_connected, NULL);
  g_object_unref (msg);

  stream_id = nice_agent_add_stream (agent, 1);
  if (stream_id == 0)
    g_error ("nice_agent_add_stream failed");
  if (!nice_agent_set_stream_name (agent, stream_id, STREAM_NAME))
    g_error ("nice_agent_set_stream_name failed");

  g_signal_connect (agent, "candidate-gathering-done",
                    G_CALLBACK (on_candidate_gathering_done), GINT_TO_POINTER (FALSE));
  g_signal_connect (agent, "component-state-changed",
                    G_CALLBACK (on_component_state_changed), NULL);

  nice_agent_attach_recv (agent, stream_id, COMPONENT_ID, ctx, on_recv, NULL);

  if (!nice_agent_gather_candidates (agent, stream_id))
    g_error ("nice_agent_gather_candidates failed");

  g_main_loop_run (loop);

  g_object_unref (agent);
  g_clear_object (&ws_session);
  g_free (local_sdp);
  g_free (peer_sdp);
  g_main_loop_unref (loop);
  return EXIT_SUCCESS;
}

int
main (int argc, char **argv)
{
  guint16 port = DEFAULT_SIG_PORT;

  if (argc < 2)
    {
      fprintf (stderr,
               "用法:\n  %s offer [信令端口]\n  %s answer <主机> <信令端口>\n",
               argv[0], argv[0]);
      return EXIT_FAILURE;
    }

  if (strcmp (argv[1], "offer") == 0)
    {
      if (argc >= 3)
        port = (guint16) strtoul (argv[2], NULL, 10);
      return run_offer (port);
    }

  if (strcmp (argv[1], "answer") == 0)
    {
      if (argc < 4)
        {
          fprintf (stderr, "用法: %s answer <host> <port>\n", argv[0]);
          return EXIT_FAILURE;
        }
      port = (guint16) strtoul (argv[3], NULL, 10);
      return run_answer (argv[2], port);
    }

  fprintf (stderr, "第一个参数必须是 offer 或 answer\n");
  return EXIT_FAILURE;
}
