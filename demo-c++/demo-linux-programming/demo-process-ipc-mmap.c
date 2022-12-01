/*************************************************************************
#	> File Name:demo-process-ipc-mmap.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月24日 星期四 20时36分22秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <sys/wait.h>
#include <sys/stat.h>

// 演示mmap和munmap基本使用
void test_basic_usage_mmap_and_munmap() {
	char *filename = "./test-ipc-mmap.tmp";
	remove(filename);
	
	int fd = open(filename, O_RDWR | O_CREAT, 0644);
	if(fd == -1) {
		perror("open错误");
		exit(1);
	}

	ftruncate(fd, 20);
	int filesize = lseek(fd, 0, SEEK_END);
	char *mmapP = (char *)mmap(NULL, filesize, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
	if(mmapP == MAP_FAILED) {
		perror("mmap错误");
		exit(1);
	}
	
	close(fd);
	
	strcpy(mmapP, "hello world - basic!\n");
	
	int result = munmap(mmapP, filesize);
	if(result == -1) {
		perror("munmap错误");
		exit(1);
	}
}

// 测试父子进程mmap通讯
void test_fork_ipc_mmap() {
	char filename[] = "./test-ipc-mmap.tmp";
	remove(filename);

	int fd = open(filename, O_RDWR | O_CREAT, 0644);
	if(fd == -1) {
		perror("open错误");
		exit(1);
	}

	ftruncate(fd, 20);

	char *mmapP = (char *)mmap(NULL, 50, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
	if(mmapP == MAP_FAILED) {
		perror("mmap错误");
		exit(1);
	}

	close(fd);

	pid_t pid = fork();
	if(pid == -1) {
		perror("fork错误");
		exit(1);
	} else if(pid == 0) {
		strcpy(mmapP, "Hello world!");
	} else if(pid > 0) {
		sleep(1);
		//write(STDOUT_FILENO, mmapP, 1);
		printf("%s\n", mmapP);
		wait(NULL);

		int result = munmap(mmapP, 50);
		if(result == -1) {
			perror("munmap错误");
			exit(1);
		}
	}	
}

int main() {
	// test_basic_usage_mmap_and_munmap();
	test_fork_ipc_mmap();

	return 0;
}
