/*
 * libsoup WebSocket 客户端：逻辑等价于
 * demo-spring-websocket/.../ApplicationTests.java 中的 contextLoads()
 *   - ws://host:port/connector?clientId=&token=
 *   - 解析首帧 JSON 数组（含 data 内嵌 JSON、type=wsNotifyClientConnected）
 *   - A 先发连接就绪后，再连 B；B 就绪后 A 发送随机文本；B 应收到 {"content":"..."}
 *
 * Build:
 *   gcc -O2 -g -Wall -Wextra -o client demo-libsoup-wsclient.c $(pkg-config --cflags --libs libsoup-2.4 glib-2.0 gio-2.0 json-c)
 *
 * Run（需先启动 Spring 应用，默认端口 8085）:
 *   ./client
 *   ./client 8085
 *   ./client 8085 127.0.0.1
 */

#include <errno.h>
#include <gio/gio.h>
#include <glib.h>
#include <json-c/json.h>
#include <libsoup/soup.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <locale.h>

typedef struct {
  // 是否有数据
  gboolean has_data;
  // 数据类型为array时的数据长度
  gint data_list_len;
  // 是否接受到Websocket服务器响应的已经连接通知
  gboolean received_ws_notify;
  // 接受到的广播消息
  gchar *broadcast_content;
} ClientStore;

typedef struct {
  GMainLoop *loop;
  // HTTP sesssion
  // 所有客户端可以共用一个session
  SoupSession *session;

  // Websocket的主机ip和端口
  const gchar *host;
  guint16 port;

  // 客户端的clientId
  gchar *client_id_a;
  gchar *client_id_b;
  // 发送的消息
  gchar *outbound;

  SoupWebsocketConnection *ws_a;
  SoupWebsocketConnection *ws_b;

  gboolean b_connect_started;
  // B客户端是否已经广播消息
  gboolean sent_broadcast;

  // Websocket返回的数据存储
  ClientStore store_a;
  ClientStore store_b;

  guint timeout_id;
  // 测试的退出码，0表示测试成功
  gint exit_code;
} TestCtx;

static void client_store_clear(ClientStore *s) {
  g_free(s->broadcast_content);
  s->broadcast_content = NULL;
  memset(s, 0, sizeof(*s));
}

/*
 * 与 ApplicationTests.newStoringHandler 一致：数组帧写入 data/notify；
 * 对象帧若含 content 则写入 broadcastContent。
 */
static void apply_payload_to_store(const gchar *payload, ClientStore *store) {
  json_object *root;

  if (!payload) {
    return;
  }

  root = json_tokener_parse(payload);
  if (!root) {
    return;
  }

  if (json_object_is_type(root, json_type_array)) {
    int n = json_object_array_length(root);
    if (n > 0) {
      gboolean received_ws_notify = FALSE;
      int i;

      for (i = 0; i < n; i++) {
        json_object *node = json_object_array_get_idx(root, i);
        json_object *jdata = NULL;

        if (json_object_object_get_ex(node, "data", &jdata) &&
            json_object_is_type(jdata, json_type_string)) {
          const char *inner = json_object_get_string(jdata);
          json_object *inner_obj = json_tokener_parse(inner);

          if (inner_obj) {
            json_object *jtype = NULL;
            if (json_object_object_get_ex(inner_obj, "type", &jtype) &&
                json_object_is_type(jtype, json_type_string) &&
                strcmp(json_object_get_string(jtype), "wsNotifyClientConnected") ==
                    0) {
              received_ws_notify = TRUE;
            }
            json_object_put(inner_obj);
          }
        }
      }
      store->has_data = TRUE;
      store->data_list_len = n;
      store->received_ws_notify = received_ws_notify;
    }
  } else if (json_object_is_type(root, json_type_object)) {
    json_object *jcontent = NULL;
    if (json_object_object_get_ex(root, "content", &jcontent) &&
        json_object_is_type(jcontent, json_type_string)) {
      g_free(store->broadcast_content);
      store->broadcast_content =
          g_strdup(json_object_get_string(jcontent));
    }
  }

  json_object_put(root);
}

static gboolean store_notify_ready(const ClientStore *s) {
  return s->has_data && s->data_list_len == 1 && s->received_ws_notify;
}

static gboolean on_deadline(gpointer user_data) {
  TestCtx *ctx = (TestCtx *)user_data;

  g_printerr("超时：未在限定时间内完成与 ApplicationTests 等价的步骤\n");
  ctx->timeout_id = 0;
  ctx->exit_code = 2;
  g_main_loop_quit(ctx->loop);
  return G_SOURCE_REMOVE;
}

static void succeed(TestCtx *ctx) {
  if (ctx->timeout_id != 0) {
    g_source_remove(ctx->timeout_id);
    ctx->timeout_id = 0;
  }
  ctx->exit_code = 0;
  g_main_loop_quit(ctx->loop);
}

static void start_connect_b(TestCtx *ctx);

static void on_b_message(SoupWebsocketConnection *conn,
                        SoupWebsocketDataType type,
                        GBytes *message,
                        gpointer user_data) {
  TestCtx *ctx = (TestCtx *)user_data;
  const gchar *data;
  gsize len;
  gchar *text;

  (void)conn;

  if (type != SOUP_WEBSOCKET_DATA_TEXT) {
    return;
  }

  data = g_bytes_get_data(message, &len);
  text = g_strndup(data, len);

  apply_payload_to_store(text, &ctx->store_b);

  if (store_notify_ready(&ctx->store_b) && !ctx->sent_broadcast && ctx->ws_a &&
      ctx->outbound) {
    soup_websocket_connection_send_text(ctx->ws_a, ctx->outbound);
    ctx->sent_broadcast = TRUE;
  }

  if (ctx->store_b.broadcast_content && ctx->outbound &&
      strcmp(ctx->store_b.broadcast_content, ctx->outbound) == 0) {
    succeed(ctx);
  }

  g_free(text);
}

static void on_b_closed(SoupWebsocketConnection *conn, gpointer user_data) {
  TestCtx *ctx = (TestCtx *)user_data;
  (void)conn;
  if (ctx->ws_b == conn) {
    ctx->ws_b = NULL;
  }
}

static void on_b_error(SoupWebsocketConnection *conn,
                       GError *error,
                       gpointer user_data) {
  (void)conn;
  (void)user_data;
  g_printerr("B WebSocket error: %s\n", error ? error->message : "?");
}

static void on_b_ws_ready(GObject *source,
                          GAsyncResult *res,
                          gpointer user_data) {
  TestCtx *ctx = (TestCtx *)user_data;
  GError *err = NULL;
  SoupWebsocketConnection *ws;

  ws = soup_session_websocket_connect_finish(SOUP_SESSION(source), res, &err);
  if (!ws) {
    g_printerr("B 握手失败: %s\n", err ? err->message : "?");
    g_clear_error(&err);
    ctx->exit_code = 1;
    g_main_loop_quit(ctx->loop);
    return;
  }

  ctx->ws_b = ws;
  g_signal_connect(ws, "message", G_CALLBACK(on_b_message), ctx);
  g_signal_connect(ws, "closed", G_CALLBACK(on_b_closed), ctx);
  g_signal_connect(ws, "error", G_CALLBACK(on_b_error), ctx);
}

static void start_connect_b(TestCtx *ctx) {
  SoupURI *suri;
  SoupMessage *msg;
  gchar *uri;

  if (ctx->b_connect_started) {
    return;
  }
  ctx->b_connect_started = TRUE;

  uri = g_strdup_printf(
      "ws://%s:%u/connector?clientId=%s", ctx->host,
      (unsigned)ctx->port, ctx->client_id_b);
  suri = soup_uri_new(uri);
  g_free(uri);
  if (!suri) {
    g_printerr("无效的 B 端 URI\n");
    ctx->exit_code = 1;
    g_main_loop_quit(ctx->loop);
    return;
  }

  /*
   * soup_message_new_from_uri(SOUP_METHOD_GET, suri)：
   *   - 由 SoupURI（此处为 ws://…/connector?clientId=…）构造一条 SoupMessage，表示一次
   *     HTTP 请求行与方法；WebSocket 在 HTTP/1.1 上通过「GET + Upgrade」完成握手，
   *     故客户端侧必须先有一条 GET 消息，libsoup 再在 soup_session_websocket_connect_async
   *     内补全 Upgrade、Connection、Sec-WebSocket-* 等与握手相关的头。
   *   - 第一个实参 SOUP_METHOD_GET：与 RFC 6455 一致，升级前请求必须为 GET。
   *   - suri 仅在本调用及紧随其后的 soup_uri_free 中使用：SoupMessage 会拷贝所需 URI
   *     信息，释放 suri 不会破坏已创建的 msg。
   *   - 返回的 msg 将交给 soup_session_websocket_connect_async；会话异步发送该请求，
   *     完成后在回调里 soup_session_websocket_connect_finish 得到 SoupWebsocketConnection。
   *   - 若分配失败返回 NULL，故下面判空。
   */
  msg = soup_message_new_from_uri(SOUP_METHOD_GET, suri);
  soup_uri_free(suri);
  if (!msg) {
    g_printerr("无法创建 B 的 SoupMessage\n");
    ctx->exit_code = 1;
    g_main_loop_quit(ctx->loop);
    return;
  }

  /*
   * soup_session_websocket_connect_async：在 SoupSession 上异步发起 WebSocket 握手（不阻塞）。
   *   - session：与 main 中 soup_session_new 对应，同一客户端内核；A、B 两次调用共用，
   *     由会话调度 DNS/TCP/TLS（若 wss）及 HTTP Upgrade。
   *   - msg：上文的 GET + ws:// URI；libsoup 会在此基础上设置/校验握手头并发出请求。
   *   - 第 3 参 origin：NULL 表示不单独设置 Sec-WebSocket-Origin（或等价策略由库默认处理）；
   *     若服务端校验 Origin，可传允许的源字符串。
   *   - 第 4 参 protocols：NULL 表示不协商子协议（Sec-WebSocket-Protocol）；否则可传
   *     字符串数组指针以声明客户端支持的子协议。
   *   - 第 5 参 cancellable：NULL 表示不可通过 GCancellable 取消本次握手；若需中途取消
   *     可传入 cancellable 并在别处 g_cancellable_cancel。
   *   - 回调 on_b_ws_ready：握手成功或失败后在主上下文中触发；成功路径内需
   *     soup_session_websocket_connect_finish 取 SoupWebsocketConnection，失败则 error 非空。
   *   - user_data ctx：传入 TestCtx，供回调里挂接 B 的 message/closed/error。
   * 紧随其后的 g_object_unref(msg)：调用方释放自己对 msg 的引用；会话在异步期间会
   *   自行保留所需生命周期，与「发起异步后立即 unref GObject」的常见 GLib 用法一致。
   */
  soup_session_websocket_connect_async(ctx->session, msg, NULL, NULL, NULL,
                                       (GAsyncReadyCallback)on_b_ws_ready, ctx);
  g_object_unref(msg);
}

static void on_a_message(SoupWebsocketConnection *conn,
                         SoupWebsocketDataType type,
                         GBytes *message,
                         gpointer user_data) {
  TestCtx *ctx = (TestCtx *)user_data;
  const gchar *data;
  gsize len;
  gchar *text;

  (void)conn;

  if (type != SOUP_WEBSOCKET_DATA_TEXT) {
    return;
  }

  data = g_bytes_get_data(message, &len);
  text = g_strndup(data, len);

  apply_payload_to_store(text, &ctx->store_a);

  if (store_notify_ready(&ctx->store_a)) {
    start_connect_b(ctx);
  }

  g_free(text);
}

static void on_a_closed(SoupWebsocketConnection *conn, gpointer user_data) {
  TestCtx *ctx = (TestCtx *)user_data;
  (void)conn;
  if (ctx->ws_a == conn) {
    ctx->ws_a = NULL;
  }
}

static void on_a_error(SoupWebsocketConnection *conn,
                       GError *error,
                       gpointer user_data) {
  (void)conn;
  (void)user_data;
  g_printerr("A WebSocket error: %s\n", error ? error->message : "?");
}

static void on_a_ws_ready(GObject *source,
                          GAsyncResult *res,
                          gpointer user_data) {
  TestCtx *ctx = (TestCtx *)user_data;
  GError *err = NULL;
  SoupWebsocketConnection *ws;

  ws = soup_session_websocket_connect_finish(SOUP_SESSION(source), res, &err);
  if (!ws) {
    g_printerr("A 握手失败: %s\n", err ? err->message : "?");
    g_clear_error(&err);
    ctx->exit_code = 1;
    g_main_loop_quit(ctx->loop);
    return;
  }

  ctx->ws_a = ws;
  g_signal_connect(ws, "message", G_CALLBACK(on_a_message), ctx);
  g_signal_connect(ws, "closed", G_CALLBACK(on_a_closed), ctx);
  g_signal_connect(ws, "error", G_CALLBACK(on_a_error), ctx);
}

static void start_connect_a(TestCtx *ctx) {
  SoupURI *suri;
  SoupMessage *msg;
  gchar *uri;

  uri = g_strdup_printf(
      "ws://%s:%u/connector?clientId=%s", ctx->host,
      (unsigned)ctx->port, ctx->client_id_a);
  suri = soup_uri_new(uri);
  g_free(uri);
  if (!suri) {
    g_printerr("无效的 A 端 URI\n");
    ctx->exit_code = 1;
    g_main_loop_quit(ctx->loop);
    return;
  }

  /* soup_message_new_from_uri：语义见 start_connect_b 中同调用处的注释块 */
  msg = soup_message_new_from_uri(SOUP_METHOD_GET, suri);
  soup_uri_free(suri);
  if (!msg) {
    g_printerr("无法创建 A 的 SoupMessage\n");
    ctx->exit_code = 1;
    g_main_loop_quit(ctx->loop);
    return;
  }

  /* soup_session_websocket_connect_async 各参数与随后 g_object_unref(msg) 见 start_connect_b */
  soup_session_websocket_connect_async(ctx->session, msg, NULL, NULL, NULL,
                                       (GAsyncReadyCallback)on_a_ws_ready, ctx);
  g_object_unref(msg);
}

static guint16 parse_port_or_die(const char *s) {
  char *end = NULL;
  long v;

  errno = 0;
  v = strtol(s, &end, 10);
  if (errno != 0 || end == s || *end != '\0' || v < 1 || v > 65535) {
    g_printerr("Invalid port: %s\n", s ? s : "(null)");
    exit(2);
  }
  return (guint16)v;
}

int main(int argc, char **argv) {
  // 设置locale为UTF-8，避免中文乱码
  setlocale(LC_ALL, "");

  TestCtx ctx;
  const gchar *host = "localhost";
  guint16 port = 8085;

  memset(&ctx, 0, sizeof(ctx));
  ctx.host = host;
  ctx.port = port;
  ctx.exit_code = 1;

  if (argc >= 2) {
    ctx.port = parse_port_or_die(argv[1]);
  }
  if (argc >= 3) {
    ctx.host = argv[2];
  }

  ctx.client_id_a = g_uuid_string_random();
  ctx.client_id_b = g_uuid_string_random();
  ctx.outbound =
      g_strdup_printf("hello-from-A-%s", g_uuid_string_random());

  /*
   * soup_session_new：创建 libsoup 的 SoupSession，即本进程的「HTTP 客户端」内核。
   *   - 后续 soup_session_websocket_connect_async 会用它发起 GET 升级请求（SoupMessage），
   *     完成握手后得到 SoupWebsocketConnection；底层仍走同一套连接/TLS/代理等策略。
   *   - 不传属性时得到默认会话（Cookie 罐、超时、SSL 等默认行为）；与 Spring 测试里
   *     共用一个 WebSocketClient 类似，这里 A、B 两次握手共用同一会话即可。
   *   - 生命周期须覆盖所有异步握手与收发：main 末尾对 session 做 g_object_unref，
   *     与上面 client_id 等字符串的 g_free 区分开（ GObject 引用计数）。
   *   - 分配失败时返回 NULL（极少见），故下面判空后直接退出。
   */
  ctx.session = soup_session_new();
  if (!ctx.session) {
    g_printerr("soup_session_new failed\n");
    return 1;
  }

  ctx.loop = g_main_loop_new(NULL, FALSE);
  /* Java 各阶段 atMost(10s)，整段留足余量 */
  ctx.timeout_id = g_timeout_add_seconds(45, on_deadline, &ctx);

  start_connect_a(&ctx);
  g_main_loop_run(ctx.loop);

  if (ctx.ws_b) {
    soup_websocket_connection_close(ctx.ws_b, SOUP_WEBSOCKET_CLOSE_NORMAL, NULL);
    g_object_unref(ctx.ws_b);
    ctx.ws_b = NULL;
  }
  if (ctx.ws_a) {
    soup_websocket_connection_close(ctx.ws_a, SOUP_WEBSOCKET_CLOSE_NORMAL, NULL);
    g_object_unref(ctx.ws_a);
    ctx.ws_a = NULL;
  }

  if (ctx.timeout_id != 0) {
    g_source_remove(ctx.timeout_id);
    ctx.timeout_id = 0;
  }

  g_main_loop_unref(ctx.loop);
  g_object_unref(ctx.session);

  client_store_clear(&ctx.store_a);
  client_store_clear(&ctx.store_b);

  g_free(ctx.client_id_a);
  g_free(ctx.client_id_b);
  g_free(ctx.outbound);

  if (ctx.exit_code == 0) {
    g_print("OK: 与 ApplicationTests.contextLoads 等价检查通过\n");
  }

  return ctx.exit_code;
}
