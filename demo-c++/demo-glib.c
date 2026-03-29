/*
 * GLib（GNOME 基础库）用法小示例：版本、字符串内存、单向链表。
 *
 * 不能只用「gcc demo-glib.c -o main」：glib.h 在 glib-2.0 的头文件目录下，不在默认 -I 路径，
 * 且链接时需要 -lglib-2.0，须用 pkg-config 带上 --cflags 与 --libs。
 *
 * 依赖：sudo apt install pkg-config libglib2.0-dev
 * 编译：gcc -Wall -Wextra demo-glib.c -o main $(pkg-config --cflags --libs glib-2.0)
 */
#include <glib.h>

int main(int argc, char **argv) {
    (void)argc;
    (void)argv;

    // 运行时 GLib 是否与编译时所依赖的版本兼容（不兼容则返回说明字符串）
    const gchar *ver_err = glib_check_version(GLIB_MAJOR_VERSION, GLIB_MINOR_VERSION, GLIB_MICRO_VERSION);
    if (ver_err != NULL) {
        g_printerr("%s\n", ver_err);
        return 1;
    }
    g_print("GLib runtime: %u.%u.%u\n", glib_major_version, glib_minor_version, glib_micro_version);

    // g_strdup / g_free：GLib 堆分配（勿对 g_strdup 结果使用 free，应配对 g_free）
    gchar *s = g_strdup("hello GLib");
    g_print("%s\n", s);
    g_free(s);

    // GList：双向链表；g_list_append 可能触发整表遍历，大量尾部插入可用 g_queue 等
    GList *list = NULL;
    list = g_list_append(list, g_strdup("first"));
    list = g_list_append(list, g_strdup("second"));
    for (GList *l = list; l != NULL; l = l->next) {
        g_print("%s ", (const gchar *)l->data);
    }
    g_print("\n");
    g_list_free_full(list, (GDestroyNotify)g_free);

    return 0;
}
