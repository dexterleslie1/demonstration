/*************************************************************************
#	> File Name:demo-signal-sigaction.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月26日 星期六 20时04分13秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <unistd.h>
#include <sys/wait.h>

void sigaction_basic_handler(int signo) {
	printf("catch signal %d\n", signo);
}

// sigaction基本使用
// ctrl+c产生SIGINT信号
void test_sigaction_basic() {
	struct sigaction newsigact, oldsigact;

	newsigact.sa_handler = sigaction_basic_handler;
	sigemptyset(&newsigact.sa_mask);
	newsigact.sa_flags = 0;

	int result = sigaction(SIGINT, &newsigact, &oldsigact);
	if(result == -1) {
		perror("sigaction error");
		exit(1);
	}

	while(1) {
		sleep(1);
	}
}

void handler_sigaction_mask(int signo) {
	printf("catch signal %d, signal will be block 5 seconds for preventing reentrance\n", signo);
	sleep(5);
}

// 特性1：sigaction.sa_flags=0时，信号捕捉函数执行期间本信号自动屏蔽
// 特性2：阻塞常规信号不支持排队特性，重复的阻塞信号将会被丢弃只保留一个
// 多次连续ctrl+c产生SIGINT信号
void test_sigaction_sa_mask() {
	struct sigaction newsigact, oldsigact;

	newsigact.sa_handler = handler_sigaction_mask;
	sigemptyset(&newsigact.sa_mask);
	newsigact.sa_flags = 0;

	int result = sigaction(SIGINT, &newsigact, &oldsigact);
	if(result == -1) {
		perror("sigaction error");
		exit(1);
	}

	while(1) {
		sleep(1);
	}
}

// 特性3：信号捕捉函数执行期间，PCB信号屏蔽集mask被替换为sigaction.sa_mask，
// 捕捉函数执行结束，PCB信号屏蔽集mask被恢复为原来值

// 使用特性3实现信号捕捉函数执行期间屏蔽其他信号
// ctrl+c产生一次SIGINT信号，ctrl+\产生多次SIGQUIT信号
void test_sigaction_mask_other_signal() {
	struct sigaction newsigact, oldsigact;

	newsigact.sa_handler = handler_sigaction_mask;
	sigemptyset(&newsigact.sa_mask);
	sigaddset(&newsigact.sa_mask, SIGQUIT);
	newsigact.sa_flags = 0;

	int result = sigaction(SIGINT, &newsigact, &oldsigact);
	if(result == -1) {
		perror("sigaction error");
		exit(1);
	}

	while(1) {
		sleep(1);
	}

}

void handler_wait_child_process(int signo) {
	pid_t pid;
	while((pid = wait(NULL)) != -1) {
		printf("child process %d terminate\n", pid);
	}
}

// 演示使用SIGCHLD信号回收子进程
void test_sigaction_wait_child_process() {
	struct sigaction newact, oldact;

	newact.sa_handler = handler_wait_child_process;
	sigemptyset(&newact.sa_mask);
	newact.sa_flags = 0;

	int result = sigaction(SIGCHLD, &newact, &oldact);
	if(result == -1) {
		perror("sigaction error");
		exit(1);
	} 

	pid_t pid;
	int i=0;
	for(i=0; i<5; i++) {
		pid = fork();
		if(pid == -1) {
			perror("fork error");
			exit(1);
		} else if(pid == 0) {
			break;
		}
	}

	if(pid > 0) {
		// 等待所有子进程结束并处理SIGCHLD信号
		while(1) {
			sleep(1);
		}
	}
}

int main() {
	// test_sigaction_basic();
	// test_sigaction_sa_mask();
	// test_sigaction_mask_other_signal();
	test_sigaction_wait_child_process();

	return 0;
}
