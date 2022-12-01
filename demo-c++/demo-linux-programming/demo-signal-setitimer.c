/*************************************************************************
#	> File Name:demo-signal-setitimer.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月25日 星期五 22时07分53秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/time.h>
#include <signal.h>

void myhandler(int sig) {
	printf("[%d] catch sig no: %d, sig: %s\n", time(NULL), sig, strsignal(sig));
}

void test_setitimer() {
	printf("[%d] start\n", time(NULL));
	signal(SIGALRM, myhandler);

	struct itimerval newval;
	// 发送SIGALRM信号时间间隔秒
	newval.it_interval.tv_sec = 1;
	newval.it_interval.tv_usec = 0;
	// 第一次发送SIGALRM信号延迟时间秒
	newval.it_value.tv_sec = 5;
	newval.it_value.tv_usec = 0;
	int result = setitimer(ITIMER_REAL, &newval, NULL);
	if(result == -1) {
		perror("setitimer error");
		exit(1);
	}

	while(1);
}

int main() {
	test_setitimer();

	return 0;
}
