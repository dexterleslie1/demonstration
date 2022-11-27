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

void test_alarm_remaining() {
	int remaining = alarm(5);
	if(remaining == 0) {
		printf("there is no previously scheduled alarm!\n");
	}
		
	sleep(3);
	remaining = alarm(5);
	printf("alarm remaining %d seconds\n", remaining);

	sleep(4);
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
	test_terminate_by_using_alarm();
	
	return 0;
}
