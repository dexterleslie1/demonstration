/*************************************************************************
#	> File Name:demo-process-orphan.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月20日 星期日 17时22分50秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

int main() {
	// 演示孤儿进程
	// 当父级进程退出后，孤儿进程的父级进程变为init进程并由init进程回收

	pid_t pid = fork();
	if(pid == -1) {
		perror("fork失败");
		exit(1);
	} else if(pid > 0) {
		printf("父进程pid=%d退出\n", getpid());
	} else if(pid == 0) {
		for(int i = 0; i< 10; i++) {
			printf("子进程pid=%d，parent id=%d\n", getpid(), getppid());
			sleep(1);
		}
	}

	return 0;
}
