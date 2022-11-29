/*************************************************************************
#	> File Name:demo-process-ipc-signal-alarm.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月25日 星期五 20时34分02秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>

void handler_alarm(int signo) {
    printf("signal number is %d\n", signo);
}
void test_alarm_signal() {
    __sighandler_t prev_sighandler = signal(SIGALRM, handler_alarm);
    if(prev_sighandler == SIG_ERR) {
        perror("signal error");
        exit(1);
    }

    alarm(2);
    printf("application will receive SIGALRM signal in 2 seconds.\n");

    sleep(10);
}

void test_alarm_remaining() {
	int remaining = alarm(5);
	if(remaining == 0) {
		printf("there is no previously scheduled alarm!\n");
	}
		
	sleep(3);
    // 剩余2秒
	remaining = alarm(5);
	printf("alarm remaining %d seconds\n", remaining);

	sleep(4);
    // 剩余1秒
	remaining = alarm(5);
	printf("alarm remaining %d seconds\n", remaining);
}

void test_terminate_by_using_alarm() {
	printf("program will terminate by using alarm in 2 seconds\n");
	alarm(2);

	sleep(10);
}

int main() {
	// test_alarm_remaining();
	// test_terminate_by_using_alarm();
    test_alarm_signal();
	
	return 0;
}
