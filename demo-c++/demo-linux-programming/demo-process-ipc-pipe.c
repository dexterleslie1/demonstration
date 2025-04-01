/*************************************************************************
#	> File Name:demo-process-ipc-pipe.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月23日 星期三 10时57分47秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/wait.h>
#include <assert.h>

// 演示读写正常状态
void test_read_write_normally() {
	int pipefd[2];

	int result = pipe(pipefd);
	if(result == -1) {
		perror("创建管道失败");
		exit(1);
	}

	pid_t pid = fork();
	if(pid > 0) {
		// 关闭读
		close(pipefd[0]);
		char content[] = "hello world!\n";
		write(pipefd[1], content, strlen(content));
		close(pipefd[1]);
		wait(NULL);
	} else if(pid == 0) {
		// 关闭写端
		close(pipefd[1]);
		char buffer[1024];
		ssize_t length = read(pipefd[0], buffer, 1024);
		write(STDOUT_FILENO, buffer, length);
		close(pipefd[0]);	
	} else {
		perror("调用fork失败");
		exit(1);
	}	
}

// 演示写端关闭
void test_write_end_closed() {
	int pipefd[2];

	int result = pipe(pipefd);
	if(result == -1) {
		perror("创建管道失败");
		exit(1);
	}

	pid_t pid = fork();
	if(pid > 0) {
		// 关闭读
		close(pipefd[0]);
		close(pipefd[1]);
		wait(NULL);
	} else if(pid == 0) {
		// 关闭写端
		close(pipefd[1]);
		char buffer[1024];
		ssize_t length = read(pipefd[0], buffer, 1024);
		assert(length == 0);
		close(pipefd[0]);	
	} else {
		perror("调用fork失败");
		exit(1);
	}	
}

// 演示读端等待写端写数据
void test_read_wait_for_write() {
	int pipefd[2];

	int result = pipe(pipefd);
	if(result == -1) {
		perror("创建管道失败");
		exit(1);
	}

	pid_t pid = fork();
	if(pid > 0) {
		// 关闭读
		close(pipefd[0]);
		char content[] = "写端等待5秒后再写数据。。。读端需要等待写端数据\n";
		printf("%s", content);
		sleep(5);
		char content2[] = "hello world!\n";
		write(pipefd[1], content2, strlen(content2));
		close(pipefd[1]);
		wait(NULL);
	} else if(pid == 0) {
		// 关闭写端
		close(pipefd[1]);
		char buffer[1024];
		ssize_t length = read(pipefd[0], buffer, 1024);
		write(STDOUT_FILENO, buffer, length);
		close(pipefd[0]);	
	} else {
		perror("调用fork失败");
		exit(1);
	}	
}

// 演示写端数据大于4k
void test_write_gt_4k() {
	int pipefd[2];

	int result = pipe(pipefd);
	if(result == -1) {
		perror("创建管道失败");
		exit(1);
	}

	pid_t pid = fork();
	if(pid > 0) {
		// 关闭读
		close(pipefd[0]);
		char buffer[4097];
		result = write(pipefd[1], buffer, 4097);
		if(result == -1) {
			perror("第一次写失败");
			exit(1);		
		}
		printf("第一次写\n");
		result = write(pipefd[1], buffer, 4097);
		if(result == -1) {
			perror("第二次写失败");
			exit(1);		
		}
		printf("第二次写\n");
		close(pipefd[1]);
		wait(NULL);
	} else if(pid == 0) {
		// 关闭写端
		close(pipefd[1]);
		sleep(0.1);
		char buffer[10000];
		ssize_t length = read(pipefd[0], buffer, 10000);
		assert(length == 4097*2);
		close(pipefd[0]);	
	} else {
		perror("调用fork失败");
		exit(1);
	}	
}

// 使用进程间通讯实现 ls | wc -l
void test_ipc_pipe() {
	int pipefd[2];

	int result = pipe(pipefd);
	if(result == -1) {
		perror("创建管道失败");
		exit(1);
	}

	pid_t pid = fork();
	if(pid == 0) {
		// 关闭读
		close(pipefd[0]);
		result = dup2(pipefd[1], STDOUT_FILENO);
		if(result == -1) {
			perror("dup2调用出错");
			exit(1);
		}
		result = execlp("ls", "ls", NULL);
		if(result == -1) {
			perror("execlp调用出错");
			exit(1);
		}	
	} else if(pid > 0) {
		// 关闭写端
		close(pipefd[1]);
		result = dup2(pipefd[0], STDIN_FILENO);
		if(result == -1) {
			perror("dup2调用出错");
			exit(1);
		}
		result = execlp("wc", "wc", "-l", NULL);
		if(result == -1) {
			perror("execlp调用出错");
			exit(1);
		}
	} else {
		perror("调用fork失败");
		exit(1);
	}	
}

// 演示兄弟进程间通许
void test_ipc_pipe_fork_child_to_child() {
	int pipefd[2];
	int result = pipe(pipefd);
	if(result == -1) {
		perror("调用pipe错误");
		exit(1);
	}

	int i = 0;
	for(i=0; i<2; i++) {
		pid_t pid = fork();
		if(pid == -1) {
			perror("调用fork错误");
			exit(1);
		}
		if(pid == 0) {
			break;
		}
	}

	if(i == 2) {
		close(pipefd[0]);
		close(pipefd[1]);
	
		wait(NULL);
		wait(NULL);
	} else if(i == 0) {
		close(pipefd[1]);
		result = dup2(pipefd[0], STDIN_FILENO);
		if(result == -1) {
			perror("dup2调用出错");
			exit(1);
		}
		result = execlp("wc", "wc", "-l", NULL);
		if(result == -1) {
			perror("execlp调用出错");
			exit(1);
		}
	} else if(i == 1) {
		close(pipefd[0]);
		result = dup2(pipefd[1], STDOUT_FILENO);
		if(result == -1) {
			perror("dup2调用出错");
			exit(1);
		}
		result = execlp("ls", "ls", NULL);
		if(result == -1) {
			perror("execlp调用出错");
			exit(1);
		}	
	}
}

// 演示管道多端写和一端读情况
void test_ipc_pipe_multiple_write_end_and_only_one_read_end() {
	int pipefd[2];
	int result = pipe(pipefd);
	if(result == -1) {
		perror("调用pipe错误");
		exit(1);
	}

	int i = 0;
	for(i=0; i<2; i++) {
		pid_t pid = fork();
		if(pid == -1) {
			perror("调用fork错误");
			exit(1);
		}
		if(pid == 0) {
			break;
		}
	}

	if(i == 2) {
		close(pipefd[1]);
	
		wait(NULL);
		wait(NULL);

		char buffer[1024];
		ssize_t length = read(pipefd[0], buffer, 1024);
		write(STDOUT_FILENO, buffer, length);
		close(pipefd[0]);
	} else if(i == 0) {
		close(pipefd[0]);
		char content[] = "first child hello world\n";
		write(pipefd[1], content, strlen(content));
		close(pipefd[1]);
	} else if(i == 1) {
		close(pipefd[0]);
		char content[] = "second child hello world\n";
		write(pipefd[1], content, strlen(content));
		close(pipefd[1]);
	}
}

int main() {
	// test_read_write_normally();
	// test_write_end_closed();
	// test_read_wait_for_write();
	// test_write_gt_4k();
	// test_ipc_pipe();
	// test_ipc_pipe_fork_child_to_child();
	test_ipc_pipe_multiple_write_end_and_only_one_read_end();

	return 0;
}
