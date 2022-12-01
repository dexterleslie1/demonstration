/*************************************************************************
#	> File Name:demo-process-ipc-signal-kill.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月25日 星期五 19时30分30秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>

void test_kill_sigkill() {
	pid_t pid = fork();
	if(pid == -1) {
		perror("fork error");
		exit(1);
	} else if(pid > 0) {
		printf("parent process %d\n", getpid());
		wait(NULL);
	} else if(pid == 0) {
		sleep(1);
		kill(getppid(), SIGKILL);
		printf("child process %d send SIGKILL signal to parent process %d\n", getpid(), getppid());
	}
}

void test_kill_sigsegv() {
	pid_t pid = fork();
	if(pid == -1) {
		perror("fork error");
		exit(1);
	} else if(pid > 0) {
		printf("parent process %d\n", getpid());
		wait(NULL);
	} else if(pid == 0) {
		sleep(1);
		kill(getppid(), SIGSEGV);
		printf("child process %d send SIGSEGV signal to parent process %d\n", getpid(), getppid());
	}
}

// sigchld信号默认被父进程忽略
void test_kill_sigchld_ignore_by_default() {
	pid_t pid = fork();
	if(pid == -1) {
		perror("fork error");
		exit(1);
	} else if(pid > 0) {
		printf("parent process %d\n", getpid());
		wait(NULL);
	} else if(pid == 0) {
		sleep(1);
		kill(getppid(), SIGCHLD);
		printf("child process %d send SIGCHLD signal to parent process %d\n", getpid(), getppid());
	}
}

int main() {
	// test_kill_sigkill();		
	// test_kill_sigsegv();
	test_kill_sigchld_ignore_by_default();

	return 0;
}
