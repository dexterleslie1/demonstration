/*************************************************************************
#	> File Name:demo-signal-catch-and-handle.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月26日 星期六 14时27分55秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <unistd.h>

void signal_handler(int signo) {
	printf("catch signal number %d\n", signo);
}

// 测试使用signal捕捉信号，使用ctrl+c产生SIGINT信号
void test_catch_with_signal() {
	signal(SIGINT, signal_handler);

	while(1) {
		sleep(1);
	}
}

int main() {
	test_catch_with_signal();

	return 0;
}
