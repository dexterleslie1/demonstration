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
	// 进程间通讯命名管道读端
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

	char buffer[1024];
	ssize_t length;
	while((length = read(fd, buffer, sizeof(buffer))) > 0) {
		write(STDOUT_FILENO, buffer, length);
	}

	return 0;
}
