/*
 * json-c 精简示例：构造 object/array、序列化、解析、按类型读取、释放。
 *
 * ---------------------------------------------------------------------------
 * 编译步骤（在 demonstration 仓库根目录或自行替换源文件路径）
 * ---------------------------------------------------------------------------
 *
 * 1) 安装开发包（Debian/Ubuntu 示例）：
 *    sudo apt install libjson-c-dev
 *
 * 2) 使用 pkg-config 链接（推荐）：
 *    gcc -std=c11 -Wall -Wextra -O2 demo-lib-json-jsonc.c $(pkg-config --cflags --libs json-c) -o main
 *
 *    若 pkg-config 找不到包名，可尝试：
 *    pkg-config --list-all | grep -i json
 *
 * 3) 运行：
 *    ./main
 *
 * ---------------------------------------------------------------------------
 * 内存与所有权（json-c）
 * ---------------------------------------------------------------------------
 * - json_object_new_* / json_tokener_parse 得到的 json_object* 引用计数为 1。
 * - json_object_object_add 会把 value 的“所有权”交给父对象：不要再对同一指针 put。
 * - json_object_array_add 同理，子元素归数组所有。
 * - 根对象用 json_object_put(root) 释放即可递归释放整棵树。
 * - json_object_to_json_string / json_object_to_json_string_ext 返回的是库内部分配的
 *   只读字符串，在下次对同一 object 调用 to_json_string* 或 put 该 object 后可能失效，
 *   需要长期保存时请自行 strdup 或立即使用完毕。
 */

#include <stdio.h>
#include <stdlib.h>

#include <json-c/json.h>

static void build_and_print_json(void) {
    /* 根对象：引用计数 1，后续挂上去的子对象由树统一管理释放。 */
    struct json_object *root = json_object_new_object();
    if (root == NULL) {
        fprintf(stderr, "json_object_new_object failed\n");
        return;
    }

    /*
     * json_object_object_add(obj, key, val)：
     * - 成功时 val 归 obj 所有，本函数之后不要再 json_object_put(val)。
     * - key 为 C 字符串，库会复制 key；val 由库持有。
     */
    json_object_object_add(root, "name", json_object_new_string("dexter"));
    json_object_object_add(root, "age", json_object_new_int(18));
    json_object_object_add(root, "admin", json_object_new_boolean(1));

    struct json_object *tags = json_object_new_array();
    if (tags == NULL) {
        fprintf(stderr, "json_object_new_array failed\n");
        // json_object_put(obj)：给 obj 的引用计数 减 1；减到 0 时，库会 释放这个节点，并递归释放 已经挂在它下面的子对象（例如你已经 object_add 上去的 name、age、admin）。
        json_object_put(root);
        return;
    }
    /* 数组元素加入后归数组所有，不要对 Create 出来的元素再 put。 */
    json_object_array_add(tags, json_object_new_string("c"));
    json_object_array_add(tags, json_object_new_string("json"));
    json_object_object_add(root, "tags", tags);

    /*
     * 序列化：JSON_C_TO_STRING_NOSLASHESCAPE 等 flag 可按需组合。
     * 返回指针指向库内部缓冲区，见文件头注释。
     */
    const char *s = json_object_to_json_string_ext(root, JSON_C_TO_STRING_PLAIN);
    if (s != NULL) {
        printf("built: %s\n", s);
    }

    json_object_put(root);
}

static void parse_and_read_json(void) {
    const char *text =
        "{\"name\":\"alice\",\"age\":20,\"admin\":false,\"tags\":[\"net\",\"db\"]}";

    /*
     * json_tokener_parse：从 NUL 结尾字符串解析出一棵 json_object 树。
     * 失败返回 NULL；更细错误可用 json_tokener_new + json_tokener_parse_ex（本示例从简）。
     */
    struct json_object *root = json_tokener_parse(text);
    if (root == NULL) {
        fprintf(stderr, "json_tokener_parse failed\n");
        return;
    }

    struct json_object *name = NULL;
    struct json_object *age = NULL;
    struct json_object *admin = NULL;
    struct json_object *tags = NULL;

    /*
     * json_object_object_get_ex：若存在 key 则 *out 指向该子节点，并返回 TRUE。
     * 返回的子指针不需要单独 put（仍属于 root 树的一部分）。
     */
    if (!json_object_object_get_ex(root, "name", &name) || name == NULL) {
        fprintf(stderr, "missing field: name\n");
        json_object_put(root);
        return;
    }
    (void)json_object_object_get_ex(root, "age", &age);
    (void)json_object_object_get_ex(root, "admin", &admin);
    (void)json_object_object_get_ex(root, "tags", &tags);

    if (json_object_is_type(name, json_type_string)) {
        printf("name=%s\n", json_object_get_string(name));
    }
    if (age != NULL && json_object_is_type(age, json_type_int)) {
        printf("age=%d\n", json_object_get_int(age));
    }
    if (admin != NULL && json_object_is_type(admin, json_type_boolean)) {
        printf("admin=%s\n", json_object_get_boolean(admin) ? "true" : "false");
    }
    if (tags != NULL && json_object_is_type(tags, json_type_array)) {
        int len = json_object_array_length(tags);
        printf("tags=");
        for (int i = 0; i < len; i++) {
            struct json_object *elem = json_object_array_get_idx(tags, i);
            if (elem != NULL && json_object_is_type(elem, json_type_string)) {
                printf("%s%s", json_object_get_string(elem), (i < len - 1) ? "," : "");
            }
        }
        printf("\n");
    }

    json_object_put(root);
}

int main(void) {
    build_and_print_json();
    parse_and_read_json();
    return 0;
}
