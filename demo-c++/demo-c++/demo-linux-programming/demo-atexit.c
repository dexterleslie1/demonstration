//
// Created by dexterleslie on 22-11-29.
//

#include <stdio.h>
#include <stdlib.h>

void handler_atexit() {
    printf("program are going to exit\n");
}
int main() {
    // 程序使用exit()或者从主函数返回退出时回调使用atexit注册的函数
    int result = atexit(handler_atexit);
    if(result != 0) {
        perror("atexit error");
        exit(1);
    }

    return 0;
}
