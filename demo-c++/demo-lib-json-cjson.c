#include <stdio.h>
#include <stdlib.h>

#include "cJSON.h"

/*
 * 一个尽量“精简但有代表性”的 cJSON 示例：
 * - 构造 JSON（object/array/string/number/bool）
 * - 序列化输出（PrintUnformatted）
 * - 解析 JSON（Parse）并按类型安全读取字段
 * - 正确释放内存（cJSON_Delete / cJSON_free）
 *
 * 重要内存规则：
 * - 通过 cJSON_Print / cJSON_PrintUnformatted 得到的 char* 需要用 cJSON_free 释放。
 * - 通过 cJSON_Parse / cJSON_CreateXXX 得到的 cJSON* 需要用 cJSON_Delete 释放整棵树。
 * - cJSON_GetObjectItemXXX 返回的是“树内节点指针”，不需要也不应该单独 free/delete。
 */
static void build_and_print_json(void) {
    /* root 是 JSON 树的根节点；后续 AddXXXToObject 会把子节点挂到这棵树上。 */
    cJSON *root = cJSON_CreateObject();
    if (root == NULL) {
        fprintf(stderr, "cJSON_CreateObject failed\n");
        return;
    }

    /*
     * AddXXXToObject 这类 API：成功时会把新节点加入 root（root 持有其生命周期）。
     * 失败时通常会返回 NULL（或内部标记失败）；本示例保持精简，不对每一步做严格错误处理。
     */
    cJSON_AddStringToObject(root, "name", "dexter");
    cJSON_AddNumberToObject(root, "age", 18);
    cJSON_AddBoolToObject(root, "admin", 1);

    /* 往对象里加一个数组字段 tags，并向数组追加两个字符串元素。 */
    cJSON *tags = cJSON_AddArrayToObject(root, "tags");
    if (tags != NULL) {
        /*
         * AddItemToArray 会接管 item 的所有权：
         * - cJSON_CreateString 返回的新节点，一旦成功加入数组，就由 root 统一释放。
         */
        cJSON_AddItemToArray(tags, cJSON_CreateString("c"));
        cJSON_AddItemToArray(tags, cJSON_CreateString("json"));
    }

    /*
     * 序列化：
     * - PrintUnformatted 输出紧凑 JSON（不含缩进/换行），适合日志或网络传输。
     * - 返回的字符串由 cJSON 内部分配，必须用 cJSON_free 释放（不要用 free）。
     */
    char *s = cJSON_PrintUnformatted(root);
    if (s == NULL) {
        fprintf(stderr, "cJSON_PrintUnformatted failed\n");
        cJSON_Delete(root);
        return;
    }

    printf("built: %s\n", s);
    cJSON_free(s);
    /* 删除根节点会递归释放整棵 JSON 树（包括 tags 数组及其元素）。 */
    cJSON_Delete(root);
}

static void parse_and_read_json(void) {
    /* 演示：从 JSON 文本解析成 cJSON 树，再按字段名读取。 */
    const char *text =
        "{\"name\":\"alice\",\"age\":20,\"admin\":false,\"tags\":[\"net\",\"db\"]}";

    /*
     * Parse 失败返回 NULL；可以用 cJSON_GetErrorPtr 获取大致出错位置（指向原始文本的某个位置）。
     * 注意：error ptr 不是稳定的“错误对象”，仅用于快速定位问题。
     */
    cJSON *root = cJSON_Parse(text);
    if (root == NULL) {
        const char *err = cJSON_GetErrorPtr();
        fprintf(stderr, "cJSON_Parse failed near: %s\n", err ? err : "(unknown)");
        return;
    }

    /*
     * 获取字段节点：
     * - GetObjectItemCaseSensitive 区分大小写；如果字段不存在返回 NULL。
     * - 返回的 cJSON* 只是 root 树内部节点的指针，生命周期由 root 管理。
     */
    const cJSON *name = cJSON_GetObjectItemCaseSensitive(root, "name");
    const cJSON *age = cJSON_GetObjectItemCaseSensitive(root, "age");
    const cJSON *admin = cJSON_GetObjectItemCaseSensitive(root, "admin");
    const cJSON *tags = cJSON_GetObjectItemCaseSensitive(root, "tags");

    /*
     * 类型安全读取：
     * - JSON 是动态类型，务必先用 cJSON_IsXXX 判断类型，再读取 valuestring/valueint 等字段。
     * - 例如字符串读取 valuestring；数字可用 valueint/valuedouble（按需要选择）。
     */
    if (cJSON_IsString(name) && name->valuestring != NULL) {
        printf("name=%s\n", name->valuestring);
    }
    if (cJSON_IsNumber(age)) {
        printf("age=%d\n", age->valueint);
    }
    if (cJSON_IsBool(admin)) {
        /* Bool 用 cJSON_IsTrue 判断即可（false/true 都是 cJSON_IsBool 为真）。 */
        printf("admin=%s\n", cJSON_IsTrue(admin) ? "true" : "false");
    }
    if (cJSON_IsArray(tags)) {
        const cJSON *tag = NULL;
        printf("tags=");
        /*
         * 遍历数组：
         * - cJSON_ArrayForEach 会顺序遍历数组元素（内部是链表结构）。
         * - 这里用 tag->next 判断是否还有下一个元素，从而打印逗号分隔。
         */
        cJSON_ArrayForEach(tag, tags) {
            if (cJSON_IsString(tag) && tag->valuestring != NULL) {
                printf("%s%s", tag->valuestring, tag->next ? "," : "");
            }
        }
        printf("\n");
    }

    /* 与构造一样：释放 root 即可释放整棵树。 */
    cJSON_Delete(root);
}

int main(void) {
    /* 两段演示相互独立：先构造并打印，再解析并读取。 */
    build_and_print_json();
    parse_and_read_json();
    return 0;
}
