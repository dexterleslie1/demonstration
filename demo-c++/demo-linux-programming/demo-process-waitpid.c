/*************************************************************************
#	> File Name:demo-process-waitpid.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月20日 星期日 20时11分08秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>
#include <unistd.h>

// waitpid 阻塞等待
void wait_with_hang() {
	int isChild = 0;
	pid_t pid2;
	int i;
	// fork两个子进程
	for(i = 0; i < 5; i++) {
		pid_t pid = fork();
		if(pid == 0) {
			// 子进程不fork
			isChild = 1;
			break;
		}
		//printf("i=%d,pid=%d,child pid=%d\n", i, getpid(), pid);
		if(!isChild && i == 1) {
			pid2 = pid;
		}
	}

	if(!isChild) {
		// 阻塞等待
		pid_t pid = waitpid(pid2, NULL, 0);
		if(pid == -1) {
			perror("waitpid错误");
			exit(1);
		}
	
		printf("主进程pid=%d，wait pid=%d退出\n", getpid(), pid);
	} else {
		sleep(i);
		printf("子进程pid=%d退出\n", getpid());
	}
}

// waitpid 非阻塞等待
void wait_without_hang() {
	int isChild = 0;
	pid_t pid2;
	int i;
	// fork两个子进程
	for(i = 0; i < 5; i++) {
		pid_t pid = fork();
		if(pid == 0) {
			// 子进程不fork
			isChild = 1;
			break;
		}
		//printf("i=%d,pid=%d,child pid=%d\n", i, getpid(), pid);
		if(!isChild && i == 1) {
			pid2 = pid;
		}
	}

	if(!isChild) {
		// 非阻塞等待
		pid_t pid;
		while((pid = waitpid(pid2, NULL, WNOHANG)) == 0) {
			printf("waitpid=%d\n", pid2);
			sleep(1);
		}
		if(pid == -1) {
			perror("waitpid错误");
			exit(1);
		}

		printf("主进程pid=%d，wait pid=%d退出\n", getpid(), pid);
	} else {
		sleep(i);
		printf("子进程pid=%d退出\n", getpid());
	}
}

// waitpid 等待多个子进程
void wait_multiple_child_process() {
	int isChild = 0;
	pid_t pid2;
	int i;
	// fork两个子进程
	for(i = 0; i < 5; i++) {
		pid_t pid = fork();
		if(pid == 0) {
			// 子进程不fork
			isChild = 1;
			break;
		}
		//printf("i=%d,pid=%d,child pid=%d\n", i, getpid(), pid);
		if(!isChild && i == 1) {
			pid2 = pid;
		}
	}

	if(!isChild) {
		pid_t pid;
		while((pid = waitpid(-1, NULL, WNOHANG)) != -1) {
			if(pid == 0) {
				sleep(1);
			} else if(pid > 0) {
				printf("等待到子进程pid=%d结束\n", pid);
			}
		}
	} else {
		sleep(i);
		printf("子进程pid=%d退出\n", getpid());
	}
}

int main() {
	// wait_with_hang();
	// wait_without_hang();	
	wait_multiple_child_process();

	return 0;
}
