/*************************************************************************
#	> File Name:demo-file-open-close.c
#	> Author: jarven
#	> Mail: whuaw@aliyun.com
#	> Created Time: 2022年11月16日 星期三 23时18分26秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

int main() {
	// 文件打开和关闭
	int fd = open("./test.tmp", O_RDONLY | O_CREAT, 0777);
	if(fd == -1) {
		perror("打开文件错误 test.tmp");
		exit(1);
	}
	int err = close(fd);
	if(err == -1) {
		perror("关闭文件错误");
		exit(1);
	}

	// todo 使用不同flag打开文件
	

	// 文件不存在则创建并指定权限
	remove("./test2.tmp");
	// 权限--------x
	fd = open("./test2.tmp", O_CREAT, 0001);
	if(fd == -1) {
		perror("打开文件错误 test2.tmp");
		exit(1);
	}
	err = close(fd);
	if(err == -1) {
		perror("关闭文件错误");
		exit(1);
	}

	return 0;
}
