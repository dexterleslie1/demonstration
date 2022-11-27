/*************************************************************************
#	> File Name:demo-signal-set.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月26日 星期六 14时04分17秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <unistd.h>

// 屏蔽SIGINT信号，使用ctrl+c产生SIGINT信号
void test() {
	sigset_t sigset, oldsigset, pendingsigset;
	
	sigemptyset(&sigset);
	sigaddset(&sigset, SIGINT);

	int result = sigprocmask(SIG_BLOCK, &sigset, &oldsigset);
	if(result == -1) {
		perror("sigprocmask error");
		exit(1);
	}
	
	while(1) {
		result = sigpending(&pendingsigset);
		if(result == -1) {
			perror("sigpending error");
			exit(1);
		}

		for(int i=1; i<32; i++) {
			if(sigismember(&pendingsigset, i))
				putchar('1');
			else putchar('0');
		}
		printf("\n");
		sleep(1);
	}
}

int main() {
	test();

	return 0;
}
