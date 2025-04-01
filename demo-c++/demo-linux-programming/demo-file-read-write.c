/*************************************************************************
#	> File Name:demo-file-read-write.c
#	> Author: jarven
#	> Mail: whuaw@aliyun.com
#	> Created Time: 2022年11月17日 星期四 21时55分14秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

int main(int argc, char *argv[]) {
	// 使用read和write函数实现文件cp
	int fdread = open(argv[1], O_RDONLY);
	if(fdread == -1) {
		perror("打开源文件错误");
		exit(1);
	}

	int fdwrite = open(argv[2], O_RDWR | O_CREAT | O_TRUNC, 0664);	
	if(fdwrite == -1) {
		perror("打开目标文件错误");
		exit(1);
	}

	char buf[1024];
	ssize_t n;
	while((n = read(fdread, buf, 1024)) != 0) {
		if(n == -1) {
			perror("读取源文件错误");
			break;
		}
		write(fdwrite, buf, n);
	}

	close(fdread);
	close(fdwrite);
	
	return 0;
}
