/*************************************************************************
#	> File Name:demo-process-fork.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月20日 星期日 11时43分25秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

// 进程间全局变量共享原则：读时共享，写时复制
int a = 100;

int main() {
	printf("pid=%d，fork程序开始执行\n", getpid());

	// 演示fork、getpid、getppid函数使用
	pid_t pid = fork();
	if(pid == -1) {
		perror("fork调用错误");
		exit(1);
	} else if(pid == 0) {
		a = 101;
		printf("子进程输出，pid=%d，parent pid=%d，a=%d\n", getpid(), getppid(), a);
	} else if(pid > 0) {
		printf("父进程输出，pid=%d, parent pid=%d，child pid=%d, a=%d\n", getpid(), getppid(), pid, a);
	}

	printf("pid=%d，程序结束执行\n", getpid());	

	return 0;
}
