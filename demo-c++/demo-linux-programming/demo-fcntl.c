/*************************************************************************
#	> File Name:demo-fcntl.c
#	> Author: jarven
#	> Mail: whuaw@aliyun.com
#	> Created Time: 2022年11月17日 星期四 23时02分13秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>

int main() {
	// 演示fcntl函数使用
	
	int flags = fcntl(STDIN_FILENO, F_GETFL);
	if(flags == -1) {
		perror("调用fcntl F_GETFL失败");
		exit(1);
	}
	
	flags |= O_NONBLOCK;
	int err = fcntl(STDIN_FILENO, F_SETFL, flags);
	if(err == -1) {
		perror("调用fcntl F_SETFL失败");
		exit(1);
	}

	int result;
	char buf[1024];
	while((result = read(STDIN_FILENO, buf, 1024)) == -1) {
		if(errno != EAGAIN) {
			perror("读取STDIN_FILENO错误");
			exit(1);
		}
		
		printf("重试\n");
		sleep(2);
	}
	
	write(STDOUT_FILENO, buf, result);
	
	return 0;
}
