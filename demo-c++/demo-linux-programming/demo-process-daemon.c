/*************************************************************************
#	> File Name:demo-process-daemon.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月27日 星期日 10时24分30秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

// 创建进程会话
void test_process_create_session() {
	pid_t pid = fork();
	if(pid == -1) {
		perror("fork error");
		exit(1);
	} else if(pid == 0) {
		printf("child process id %d\n", getpid());
		printf("child process group id %d\n", getpgid(0));
		printf("child process session id %d\n", getsid(0));

		setsid();

		printf("child process id %d after create process session\n", getpid());
		printf("child process group id %d after create process session\n", getpgid(0));
		printf("child process session id %d after create process session\n", getsid(0));
	}
}

// 创建daemmon进程
void test_create_daemon_process() {
	pid_t pid = fork();
	if(pid == -1) {
		perror("fork error");
		exit(1);
	} else if(pid == 0) {
		int result = setsid();
		if(result == -1) {
			perror("setsid error");
			exit(1);
		}

		// 模拟业务代码进程不退出
		while(1) {
			sleep(1);
		}
	}
}

int main() {
	// test_process_create_session();
	test_create_daemon_process();

	return 0;
}
