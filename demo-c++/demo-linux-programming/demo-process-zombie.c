/*************************************************************************
#	> File Name:demo-process-zombie.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月20日 星期日 18时40分31秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

int main() {
	// 演示僵尸进程

	pid_t pid = fork();
	if(pid == -1) {
		perror("调用fork失败");
		exit(1);
	} else if(pid > 0) {
		for(int i = 0; i < 30; i++) {
			printf("父级进程 pid=%d, child pid=%d\n", getpid(), pid);
			sleep(1);
		}
	} else if(pid == 0) {
		printf("子进程 pid=%d，parent pid=%d\n", getpid(), getppid());
	}
	return 0;
}
