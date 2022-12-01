/*************************************************************************
#	> File Name:demo-process-wait.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月20日 星期日 19时05分25秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>

int main() {
	// 演示fork wait
	pid_t pid = fork();
	if(pid == -1) {
		perror("fork失败");
		exit(1);
	} else if(pid > 0) {
		int status;
		pid_t waitpid = wait(&status);
		if(WIFEXITED(status)) {
			printf("父级进程输出：子进程正常退出wait pid=%d，退出状态码=%d\n", waitpid, WEXITSTATUS(status));
		} else if(WIFSIGNALED(status)) {
			printf("父级进程输出：子进程signal退出wait pid=%d，signal=%d\n", waitpid, WTERMSIG(status));
		}
	} else if(pid == 0) {
		sleep(30);
		printf("子进程 pid=%d\n", getpid());
		return 88;
	}

	return 0;
}
