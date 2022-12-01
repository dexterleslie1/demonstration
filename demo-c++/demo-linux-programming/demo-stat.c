/*************************************************************************
#	> File Name:demo-stat.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月19日 星期六 13时15分08秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/stat.h>

int main() {
	// 创建文件
	char filename[] = "./testfile.tmp";
	remove(filename);
	int fd = open(filename, O_CREAT | O_RDWR, 0644);
	if(fd == -1) {
		perror("创建文件失败");
		exit(1);
	}

	char filecontent[] = "Hello world!";
	int result = write(fd, filecontent, strlen(filecontent));
	if(result == -1) {
		perror("写文件错误");
		exit(1);
	}

	close(fd);

	struct stat statbuf;
	result = stat(filename, &statbuf);
	if(result == -1) {
		perror("获取文件stat错误");
		exit(1);
	}
	// 获取文件大小
	printf("文件大小：%ld\n", statbuf.st_size);
	
	// 判断是否文件类型
	if(S_ISREG(statbuf.st_mode)) {
		printf("是普通文件\n");
	}

	return 0;
}
