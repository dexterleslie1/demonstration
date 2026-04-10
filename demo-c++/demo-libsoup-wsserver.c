/*
 * libsoup-2.4 WebSocket 示例服务器：
 *   - WebSocket 路径 `/connector`
 *   - 从握手 URL 读取查询参数 `clientId`（UTF-8 百分号解码）
 *   - 无 clientId → 关闭连接，原因「没有提供clientId」
 *   - 有 clientId → 发送首帧 JSON（单元素数组，data 内为嵌套 JSON 字符串）
 *   - 文本消息：用 json-c 解析 JSON，若 action == "MESSAGE" 且 toUser 非空则转发
 *   - 传输错误 / 连接关闭：打日志（关闭时带 clientId）
 *
 * Build:
 *   gcc -O2 -g -Wall -Wextra -o server demo-libsoup-wsserver.c \
 *     $(pkg-config --cflags --libs libsoup-2.4 glib-2.0 gio-2.0 json-c)
 *
 * Run:
 *   ./server 8085
 *
 * 浏览器控制台示例（须在 URL 中带 clientId）:
 *   const ws = new WebSocket('ws://' + location.host + '/connector?clientId=' + encodeURIComponent('u1'));
 *   ws.onmessage = e => console.log(e.data);
 */

#include <errno.h>
#include <gio/gio.h>
#include <glib.h>
#include <libsoup/soup.h>
#include <json-c/json.h>
#include <signal.h>
#include <stdlib.h>
#include <string.h>
#include <locale.h>

/* 入站 JSON 中与路由相关的字段名 */
static const char KEY_ACTION[] = "action";

/*
 * 连接建立后首帧：单元素数组，元素为含字符串字段 data 的对象；
 * data 的值本身是 JSON 字符串，内含 type: wsNotifyClientConnected。
 */
static const char WS_NOTIFY_CLIENT_CONNECTED[] =
    "[{\"data\":\"{\\\"type\\\":\\\"wsNotifyClientConnected\\\"}\"}]";

typedef struct {
  GMutex lock;
  /* clientId -> SoupWebsocketConnection*（value 不接管所有权） */
  GHashTable *by_client_id;
} AppState;

static GMainLoop *g_loop = NULL;

static guint16 parse_port_or_die(const char *s) {
  char *end = NULL;
  errno = 0;
  long v = strtol(s, &end, 10);
  if (errno != 0 || end == s || *end != '\0' || v < 1 || v > 65535) {
    g_printerr("Invalid port: %s\n", s ? s : "(null)");
    exit(2);
  }
  /*
   * guint16：GLib（glib/gtypes.h，经 <glib.h> 引入）定义的无符号 16 位整数，位宽固定为 16，
   * 与标准头中的 uint16_t 同类，用于 GLib/GIO/libsoup 等 API 中与「短无符号」相关的形参/返回值。
   * TCP/UDP 端口在协议里即为 16 位无符号（1～65535），上面已用 long 排除非法值。
   * strtol 得到的是 long，此处 (guint16)v 在已保证 v ∈ [1,65535] 的前提下，将端口收窄为
   * 与网络端口语义一致的类型，供后续绑定监听等调用使用。
   */
  return (guint16)v;
}

static void handle_sigint(int signo) {
  /*
   * (void)signo：POSIX signal 处理函数原型为 void handler(int sig)，形参 signo 会传入具体信号编号
   *（此处为 SIGINT）。本处理逻辑里不需要区分信号类型，故不使用 signo。
   * 若完全不引用 signo，在开启 -Wunused-parameter 等告警时编译器会报「未使用形参」。
   * 将 signo 强制转换为 void 是一条无副作用的表达式语句，语义上表示「有意忽略该值」，
   * 从而消除告警，同时保留与 signal(2)/sigaction 兼容的函数签名。
   */
  (void)signo;
  /*
   * g_loop 在 main 里 g_main_loop_new 之后指向全局主循环；listen 成功并打印提示后才赋值，
   * 若在极早阶段收到 SIGINT，g_loop 可能仍为 NULL，故先判空再使用。
   * g_main_loop_quit：向 GMainLoop 投递「退出」请求，使阻塞在 g_main_loop_run 中的线程返回，
   * 从而走后续清理（unref server、销毁哈希表等）并正常结束进程；相当于用 Ctrl+C 优雅停机。
   */
  if (g_loop) {
    g_main_loop_quit(g_loop);
  }
}

/* 对 clientId 做百分号解码；必要时再解一次以处理仍含转义序列的值 */
static gchar *maybe_url_decode_client_id(const gchar *raw) {
  gchar *once;
  gchar *twice;

  if (!raw || !*raw) {
    return NULL;
  }
  once = g_uri_unescape_string(raw, NULL);
  if (!once) {
    return g_strdup(raw);
  }
  twice = g_uri_unescape_string(once, NULL);
  if (twice) {
    g_free(once);
    return twice;
  }
  return once;
}

/* 从握手 URL 查询串解析 clientId（等价于常见 Web 框架里的 getParameter("clientId")） */
static gchar *parse_client_id_from_connection(SoupWebsocketConnection *conn) {
  SoupURI *uri = soup_websocket_connection_get_uri(conn);
  const gchar *query;
  GHashTable *form = NULL;
  gchar *out = NULL;

  if (!uri) {
    return NULL;
  }

  query = soup_uri_get_query(uri);
  if (!query || !*query) {
    return NULL;
  }

  form = soup_form_decode(query);
  if (!form) {
    return NULL;
  }

  {
    const gchar *raw = g_hash_table_lookup(form, "clientId");
    if (raw && *raw) {
      out = maybe_url_decode_client_id(raw);
    }
  }

  g_hash_table_destroy(form);
  return out;
}

static void send_routed_message(AppState *st,
                                const gchar *to_user,
                                const gchar *from_user,
                                const gchar *content) {
  GPtrArray *snapshot = NULL;
  guint i;
  gchar *payload = NULL;
  struct json_object *root = NULL;
  const char *json_text;

  root = json_object_new_object();
  if (!root) {
    return;
  }
  json_object_object_add(root, "fromUser",
                           json_object_new_string(from_user ? from_user : ""));
  json_object_object_add(root, "content",
                           json_object_new_string(content ? content : ""));
  json_text = json_object_to_json_string_ext(root, JSON_C_TO_STRING_PLAIN);
  payload = g_strdup(json_text ? json_text : "{}");
  json_object_put(root);
  if (!payload) {
    return;
  }

  snapshot = g_ptr_array_new_with_free_func(NULL);

  g_mutex_lock(&st->lock);
  {
    SoupWebsocketConnection *target =
        g_hash_table_lookup(st->by_client_id, to_user);
    if (target) {
      g_ptr_array_add(snapshot, target);
    }
  }
  g_mutex_unlock(&st->lock);

  if (snapshot->len == 0) {
    g_print("[ws] 用户尝试给用户 %s 发送消息，但不在线\n", to_user);
  }

  for (i = 0; i < snapshot->len; i++) {
    SoupWebsocketConnection *c = g_ptr_array_index(snapshot, i);
    if (c && soup_websocket_connection_get_state(c) == SOUP_WEBSOCKET_STATE_OPEN) {
      soup_websocket_connection_send_text(c, payload);
    }
  }

  g_ptr_array_free(snapshot, TRUE);
  g_free(payload);
}

static void on_ws_message(SoupWebsocketConnection *conn,
                          SoupWebsocketDataType type,
                          GBytes *message,
                          gpointer user_data) {
  AppState *st = (AppState *)user_data;

  if (type == SOUP_WEBSOCKET_DATA_TEXT) {
    gsize len = 0;
    const gchar *data = g_bytes_get_data(message, &len);
    gchar *text;
    struct json_object *root = NULL;
    struct json_object *j_action = NULL;
    struct json_object *j_to = NULL;
    struct json_object *j_content = NULL;
    const gchar *from_id;

    if (!data) {
      return;
    }

    text = g_strndup(data, len);
    g_print("[ws] 收到来自客户消息：%s\n", text);

    root = json_tokener_parse(text);
    if (!root) {
      g_printerr("[ws] JSON parse failed\n");
    } else {
      const char *action = NULL;
      const char *to_user = NULL;
      const char *content = NULL;

      if (json_object_object_get_ex(root, KEY_ACTION, &j_action) && j_action &&
          json_object_is_type(j_action, json_type_string)) {
        action = json_object_get_string(j_action);
      }
      if (action && strcmp(action, "MESSAGE") == 0) {
        (void)json_object_object_get_ex(root, "toUser", &j_to);
        (void)json_object_object_get_ex(root, "content", &j_content);
        if (j_to && json_object_is_type(j_to, json_type_string)) {
          to_user = json_object_get_string(j_to);
        }
        if (j_content && json_object_is_type(j_content, json_type_string)) {
          content = json_object_get_string(j_content);
        }
        if (to_user && *to_user) {
          from_id = g_object_get_data(G_OBJECT(conn), "client-id");
          send_routed_message(st, to_user, from_id, content);
        }
      }
      json_object_put(root);
    }

    g_free(text);
    return;
  }

  if (type == SOUP_WEBSOCKET_DATA_BINARY) {
    g_print("[ws] binary frame ignored (handler is text-only)\n");
  }
}

static void on_ws_closed(SoupWebsocketConnection *conn, gpointer user_data) {
  AppState *st = (AppState *)user_data;
  gchar *cid = g_object_steal_data(G_OBJECT(conn), "client-id");

  if (cid) {
    g_mutex_lock(&st->lock);
    {
      SoupWebsocketConnection *cur = g_hash_table_lookup(st->by_client_id, cid);
      if (cur == conn) {
        g_hash_table_remove(st->by_client_id, cid);
      }
    }
    g_mutex_unlock(&st->lock);
    /* 连接关闭时的调试输出 */
    g_print("[ws] session关闭后回调 sessionId %p clientId %s\n", (void *)conn,
            cid);
    g_free(cid);
  } else {
    g_print("[ws] closed sessionId %p (无 clientId，例如已被新连接替换)\n",
            (void *)conn);
  }
}

static void on_ws_error(SoupWebsocketConnection *conn,
                        GError *error,
                        gpointer user_data) {
  (void)conn;
  (void)user_data;
  g_printerr("[ws] error: %s\n", error ? error->message : "(unknown)");
}

/*
 * 注册 clientId：若同一 clientId 已在线，则关闭旧连接（单端在线 / 挤下线）。
 */
static void register_client_id(AppState *st,
                               SoupWebsocketConnection *conn,
                               const gchar *client_id) {
  g_mutex_lock(&st->lock);
  {
    SoupWebsocketConnection *old = g_hash_table_lookup(st->by_client_id, client_id);
    if (old && old != conn) {
      g_hash_table_remove(st->by_client_id, client_id);
      g_mutex_unlock(&st->lock);

      g_object_steal_data(G_OBJECT(old), "client-id");
      soup_websocket_connection_close(old, SOUP_WEBSOCKET_CLOSE_NORMAL,
                                      "replaced by new connection");

      g_mutex_lock(&st->lock);
    }
    g_hash_table_insert(st->by_client_id, g_strdup(client_id), conn);
  }
  g_mutex_unlock(&st->lock);

  g_object_set_data_full(G_OBJECT(conn), "client-id", g_strdup(client_id), g_free);
}

static void on_ws_connected(SoupServer *server,
                            SoupWebsocketConnection *conn,
                            const char *path,
                            SoupClientContext *client,
                            gpointer user_data) {
  (void)server;
  (void)path;

  AppState *st = (AppState *)user_data;
  gchar *client_id = parse_client_id_from_connection(conn);
  const gchar *host = soup_client_context_get_host(client);

  g_print("[ws] connected from %s\n", host ? host : "?");

  if (!client_id || !*client_id) {
    g_print("[ws] 服务器端主动断开链接，因为没有提供clientId\n");
    soup_websocket_connection_close(conn, SOUP_WEBSOCKET_CLOSE_NORMAL,
                                      "没有提供clientId");
    g_free(client_id);
    return;
  }

  register_client_id(st, conn, client_id);

  /*
   * WebSocket 连接的核心事件入口。
   *
   * libsoup 的 SoupWebsocketConnection 是基于 GLib main loop 的：这些信号回调通常在
   * 运行该连接所依附的 GMainContext 的线程里触发（常见就是主线程事件循环）。
   * 因此回调里应尽量做到：
   * - 不要做长时间阻塞（I/O、sleep、重计算），否则会阻塞整个事件循环导致所有连接卡顿；
   * - 对共享状态（例如 st->by_client_id / st->by_conn）做并发保护；同时避免在持锁状态下
   *   调用可能触发更多信号/回调的操作（例如 close/send），以免产生重入或死锁。
   *
   * 我们分别监听：
   * - "message": 收到客户端消息（文本/二进制）。这是应用层协议处理入口。
   * - "closed": 连接被关闭（对端关闭、服务端关闭、或错误导致关闭）。用于做清理/注销映射，
   *   防止 st 中残留失效连接指针。
   * - "error": 发生传输/协议层错误。注意很多场景下 error 之后仍会再触发 closed，因此 error
   *   回调应该是“记录/标记/触发关闭”，而清理逻辑以 closed 为准更稳妥。
   *
   * user_data 传入 st：要求 AppState 生命周期覆盖所有活动连接（通常 st 在进程退出前都有效）。
   */
  g_signal_connect(conn, "message", G_CALLBACK(on_ws_message), st);
  g_signal_connect(conn, "closed", G_CALLBACK(on_ws_closed), st);
  g_signal_connect(conn, "error", G_CALLBACK(on_ws_error), st);

  soup_websocket_connection_send_text(conn, WS_NOTIFY_CLIENT_CONNECTED);

  g_print("[ws] %s 连接websocket服务器\n", client_id);
  g_free(client_id);
}

static void on_http_root(SoupServer *server,
                         SoupMessage *msg,
                         const char *path,
                         GHashTable *query,
                         SoupClientContext *client,
                         gpointer user_data) {
  (void)server;
  (void)path;
  (void)query;
  (void)client;
  (void)user_data;

  static const char body[] =
      "<!doctype html><meta charset='utf-8'>"
      "<title>libsoup websocket demo</title>"
      "<h3>libsoup websocket — 路径 `/connector`</h3>"
      "<p>必须在查询参数中提供 <code>clientId</code>：</p>"
      "<pre>"
      "const ws = new WebSocket(\n"
      "  'ws://' + location.host + '/connector?clientId=' + encodeURIComponent('u1')\n"
      ");\n"
      "ws.onmessage = e => console.log(e.data);\n"
      "</pre>";

  soup_message_set_status(msg, SOUP_STATUS_OK);
  soup_message_headers_set_content_type(msg->response_headers, "text/html",
                                       NULL);
  soup_message_body_append(msg->response_body, SOUP_MEMORY_STATIC, body,
                           sizeof(body) - 1);
  soup_message_body_complete(msg->response_body);
}

int main(int argc, char **argv) {
  // 设置locale为UTF-8，避免中文乱码
  setlocale(LC_ALL, "");

  guint16 port = 8080;
  if (argc >= 2) {
    port = parse_port_or_die(argv[1]);
  }

  signal(SIGINT, handle_sigint);

  AppState st;
  memset(&st, 0, sizeof(st));
  g_mutex_init(&st.lock);
  st.by_client_id = g_hash_table_new_full(g_str_hash, g_str_equal, g_free, NULL);

  /*
   * soup_server_new：按「属性名, 值, …」的可变参数列表创建 SoupServer，以 NULL 结束。
   * SOUP_SERVER_SERVER_HEADER：设置 HTTP 响应里 Server 头字段的值（此处为 "demo-libsoup-wsserver"），
   * 客户端可见，便于识别服务实现；与监听端口、是否 TLS 等无关，那些在 listen 阶段再配。
   * 分配失败或内部错误时返回 NULL（libsoup 2.x 下极少见，仍应判空）；成功则返回的对象需用
   * g_object_unref 在退出路径释放（见 main 末尾）。
   */
  SoupServer *server = soup_server_new(SOUP_SERVER_SERVER_HEADER,
                                       "demo-libsoup-wsserver", NULL);
  if (!server) {
    g_printerr("Failed to create SoupServer\n");
    return 1;
  }

  /*
   * soup_server_add_handler：为「普通 HTTP」请求注册路径与回调（与下面的 WebSocket 处理器独立）。
   *   - path "/"：仅精确匹配站点根路径的 GET 等请求；浏览器打开 http://host:port/ 时进入 on_http_root。
   *   - on_http_root：SoupServerCallback，在内部收到匹配消息时调用，负责写 SoupMessage 的响应体/状态码。
   *   - user_data 为 NULL：回调里用不到额外上下文（on_http_root 里 user_data 已 (void) 忽略）。
   *   - 最后一个 NULL：无 GDestroyNotify，因 user_data 非堆上自有数据，无需在处理器移除时释放。
   * WebSocket 走 soup_server_add_websocket_handler，不会与 "/" 的普通 HTTP 处理混淆。
   */
  soup_server_add_handler(server, "/", on_http_root, NULL, NULL);

  /*
   * soup_server_add_websocket_handler：在 path 上处理 WebSocket 升级（HTTP Upgrade），握手成功后调 callback。
   *   - "/connector"：客户端应连接 ws://host:port/connector?clientId=...；与普通 HTTP 的 "/" 路径分离。
   *   - origin NULL：不校验 Origin 头（生产环境常设为允许的源字符串以限制跨站连接）。
   *   - protocols NULL：不声明/筛选 WebSocket 子协议（Sec-WebSocket-Protocol）；需要时可传字符串数组。
   *   - on_ws_connected：SoupServerWebsocketCallback，在连接已建立、得到 SoupWebsocketConnection 时调用，
   *     内部解析 clientId、注册哈希表并挂 message/closed/error 信号。
   *   - &st：作为 user_data 传入回调（on_ws_connected 里转为 AppState *），供多连接共享 clientId 表与互斥锁。
   *   - 末尾 NULL：无 GDestroyNotify；st 是 main 栈上对象，生命周期由 main 管理，绝不能在此处注册释放函数。
   */
  soup_server_add_websocket_handler(server,
                                    "/connector",
                                    NULL,
                                    NULL,
                                    on_ws_connected,
                                    &st,
                                    NULL);

  GError *error = NULL;
  if (!soup_server_listen_all(server, port, 0, &error)) {
    g_printerr("Listen failed on port %u: %s\n", (unsigned)port,
               error ? error->message : "(unknown)");
    g_clear_error(&error);
    g_object_unref(server);
    g_hash_table_destroy(st.by_client_id);
    g_mutex_clear(&st.lock);
    return 1;
  }

  g_print("HTTP  : http://127.0.0.1:%u/\n", (unsigned)port);
  g_print("WS    : ws://127.0.0.1:%u/connector?clientId=...\n", (unsigned)port);
  g_print("Ctrl+C to stop.\n");

  g_loop = g_main_loop_new(NULL, FALSE);
  g_main_loop_run(g_loop);

  g_main_loop_unref(g_loop);
  g_loop = NULL;

  soup_server_disconnect(server);
  g_object_unref(server);

  g_hash_table_destroy(st.by_client_id);
  g_mutex_clear(&st.lock);
  return 0;
}
