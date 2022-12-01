/*************************************************************************
#	> File Name:demo-process-ipc-fifo-write-end.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月24日 星期四 09时31分46秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/stat.h>
#include <errno.h>
#include <fcntl.h>

int main() {
	// 进程间通讯命名管道写端
	char filename[] = "./test-ipc-fifo.tmp";
	int result = mkfifo(filename, 0644);
	if(result == -1 && errno != EEXIST) {
		perror("mkfifo错误");
		exit(1);
	}

	int fd = open(filename, O_RDWR);
	if(fd == -1) {
		perror("open错误");
		exit(1);
	}

	for(int i=0; i<10; i++) {
		char content[1024];
		sprintf(content, "消息%d\n", i);
		ssize_t length = write(fd, content, strlen(content));
		if(length == -1) {
			perror("write错误");
			exit(1);
		}
		printf("写消息: %s", content);
		sleep(1);
	}

	return 0;
}
