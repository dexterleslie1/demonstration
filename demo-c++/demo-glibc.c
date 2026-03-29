/*
 * glibc（GNU C Library）用法小示例：版本、标准 C 排序、时间与环境变量。
 * 编译：gcc -Wall -Wextra -o demo-glibc demo-glibc.c
 */
#define _GNU_SOURCE
#include <gnu/libc-version.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

static int cmp_int(const void *a, const void *b) {
    int x = *(const int *)a;
    int y = *(const int *)b;
    return (x > y) - (x < y);
}

int main(void) {
    // gnu_get_libc_version：返回当前进程链接的 glibc 版本字符串
    printf("glibc version: %s\n", gnu_get_libc_version());

    const char *s = "hello glibc";
    // string.h：strlen 等由 glibc 实现
    printf("strlen(\"%s\") = %zu\n", s, strlen(s));

    int arr[] = {3, 1, 4, 1, 5};
    size_t n = sizeof(arr) / sizeof(arr[0]);
    // stdlib.h：qsort
    qsort(arr, n, sizeof(arr[0]), cmp_int);
    printf("qsort: ");
    for (size_t i = 0; i < n; i++) {
        printf("%d%c", arr[i], i + 1 < n ? ' ' : '\n');
    }

    // time.h：POSIX clock_gettime（glibc 提供）
    struct timespec ts;
    if (clock_gettime(CLOCK_MONOTONIC, &ts) == 0) {
        printf("CLOCK_MONOTONIC: %ld.%09ld\n", (long)ts.tv_sec, ts.tv_nsec);
    }

    // stdlib.h：getenv
    const char *home = getenv("HOME");
    printf("HOME=%s\n", home != NULL ? home : "(unset)");

    return 0;
}
