#include <errno.h>
#include <stdio.h>
#include <string.h>

int main(void) {
    errno = 0;
    FILE *fp = fopen("/__this_path_should_not_exist__", "r");
    if (fp == NULL) {
        // errno：失败时由库/系统调用设置的整型错误码（成功时一般不应依赖其值，需先判断返回值）
        printf("errno = %d\n", errno);
        // strerror(errno)：把 errno 转成可读说明字符串（不自动打印）
        printf("strerror(errno) = %s\n", strerror(errno));
        // perror("...")：向 stderr 输出 "前缀: strerror(errno)\n"
        perror("fopen");
    } else {
        fclose(fp);
    }
    return 0;
}
