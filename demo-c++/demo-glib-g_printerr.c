/*
 * demo-glib-g_printerr.c — g_printerr 用法示例
 *
 * 编译:
 *   gcc -std=c11 -Wall -Wextra -o demo-glib-g_printerr demo-glib-g_printerr.c \
 *     $(pkg-config --cflags --libs glib-2.0)
 */

#include <glib.h>
#include <locale.h>

int main(void) {
  // 设置locale为UTF-8，避免中文乱码
  setlocale(LC_ALL, "");

  /*
   * g_printerr：GLib 提供的「错误信息」输出函数，语义上对应标准错误流 stderr。
   *   - 与 fprintf(stderr, fmt, ...) 类似，支持 printf 风格格式串。
   *   - 在 Windows 上会把消息转为 UTF-8 再输出；POSIX 上通常就是写到 stderr。
   * 与 g_print 的区别：g_print 默认走 stdout（且受 G_MESSAGES_DEBUG 等影响），
   * g_printerr 专用于用户可见的错误/告警，便于与正常日志分离（重定向时 ./a.out >out 2>err）。
   */
  g_printerr("demo: 默认写到 stderr 的一行\n");

  int code = 42;
  g_printerr("带格式: code=%d, msg=%s\n", code, "something failed");

  /*
   * 习惯上错误信息末尾加 '\n'，否则可能与 shell 提示符粘在同一行。
   * 库代码里常见写法与 demo-libsoup-wsserver 一致：g_printerr("...\n", ...);
   */
  return 0;
}
