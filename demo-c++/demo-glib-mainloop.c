/*
 * GLib 事件循环小示例：GMainContext、GSource、GMainLoop。
 *
 * 三者关系（简化）：
 *   GMainContext 维护一组已 attach 的 GSource，在每次「迭代」里：准备检查哪些源就绪 → poll（可阻塞）
 *   → dispatch 就绪源的回调。GMainLoop 只是把「反复对某个 context 做迭代」包成 run/quit API。
 *
 * - GMainContext
 *   每个 context 有独立的一套 source 与轮询状态。NULL 常表示「默认上下文」
 *   （g_main_context_default()，单进程通常共用一个默认循环）。
 *   本示例用 g_main_context_new() 刻意与默认上下文分离，便于理解：只有 attach 到此 ctx 的源
 *   才会在本循环中被处理；g_idle_add 等便捷 API 默认挂的是默认 context，与此 ctx 无关。
 *
 * - GSource
 *   抽象「事件源」：超时、idle、socket 可读等。典型用法：g_*_source_new → g_source_set_callback
 *   → g_source_attach(context) → g_source_unref。attach 后 context 会持有 source 的引用，
 *   因此创建者通常 unref 一次，避免泄漏；source 从 context 移除时引用计数归零才会销毁。
 *   回调返回 G_SOURCE_REMOVE 表示移除该源；G_SOURCE_CONTINUE 表示保留并等待下次就绪。
 *
 * - GMainLoop
 *   g_main_loop_new(context, is_running)：绑定到指定 context；第二个参数一般为 FALSE，
 *   表示尚未进入 run；若传 TRUE 表示在 run 之前 loop 就视为「已在运行」（少见）。
 *   g_main_loop_run 内部等价于在循环里调用 g_main_context_iteration(ctx, TRUE) 直到 quit。
 *   g_main_loop_quit 使下一次迭代后 run 返回（可在任意线程对绑定同一 loop 调用，需注意线程安全约定）。
 *
 * 依赖：sudo apt install pkg-config libglib2.0-dev
 * 编译：gcc -Wall -Wextra demo-glib-mainloop.c -o main $(pkg-config --cflags --libs glib-2.0)
 *
 * 若运行时中文乱码：终端需 UTF-8（如 LANG=zh_CN.UTF-8）；程序入口已 setlocale 跟随环境。
 */
#include <glib.h>
#include <locale.h>

/* 传给超时回调的 user_data：携带 loop 指针以便在回调里 quit，以及自定义计数。 */
typedef struct {
    GMainLoop *loop;
    guint n;
} AppState;

/*
 * Idle 源：在「当前没有更高优先级阻塞等待」时尽快 dispatch，优先级通常高于普通 timeout，
 * 适合把「下一轮事件循环里再做」的轻量工作塞进去。此处仅演示一次性 idle。
 */
static gboolean on_idle(gpointer user_data) {
    (void)user_data;
    g_print("idle GSource（优先于阻塞等待，先 dispatch 一次）\n");
    /* 返回 REMOVE：本源从 context 上移除，不再触发。 */
    return G_SOURCE_REMOVE;
}

static gboolean on_timeout(gpointer user_data) {
    AppState *st = user_data;
    st->n++;
    g_print("timeout GSource: tick %u\n", st->n);
    if (st->n >= 3) {
        /* 请求主循环结束；run 会在当前这一轮 dispatch 完成后返回。 */
        g_main_loop_quit(st->loop);
        /* 同时移除本 timeout 源，避免 quit 后仍留在 context 上（虽 run 已结束，仍属干净收尾）。 */
        return G_SOURCE_REMOVE;
    }
    /* 保留源，下次间隔到期再次回调。 */
    return G_SOURCE_CONTINUE;
}

int main(int argc, char **argv) {
    (void)argc;
    (void)argv;

    // 设置locale为UTF-8，避免中文乱码
    setlocale(LC_ALL, "");

    /* 新建独立上下文，引用计数 1；与 g_main_context_default() 不同栈。 */
    GMainContext *ctx = g_main_context_new();
    /* 主循环对象绑定到 ctx；之后 run 只驱动这个 context。 */
    GMainLoop *loop = g_main_loop_new(ctx, FALSE);
    AppState st = { loop, 0 };

    /*
     * Idle 源：g_idle_source_new 创建后引用计数为 1。
     * 第四个参数 destroy_notify：若回调的 user_data 需要随 source 销毁而释放，可传 g_free 等；
     * 此处无堆分配 user_data，传 NULL。
     */
    GSource *idle = g_idle_source_new();
    g_source_set_callback(idle, on_idle, NULL, NULL);
    g_source_attach(idle, ctx);
    /* attach 后 context 已接管引用，此处 unref 配对 new，不泄漏。 */
    g_source_unref(idle);

    /* g_timeout_source_new 参数为毫秒间隔。 */
    GSource *tmo = g_timeout_source_new(150);
    g_source_set_callback(tmo, on_timeout, &st, NULL);
    g_source_attach(tmo, ctx);
    g_source_unref(tmo);

    /* 阻塞在此，直到某处 g_main_loop_quit（本例在 on_timeout 第三次）。 */
    g_main_loop_run(loop);

    g_main_loop_unref(loop);
    /* 与 new 配对；context 销毁时会清理仍 attach 的源（若未 REMOVE）。 */
    g_main_context_unref(ctx);
    return 0;
}
