/*************************************************************************
#	> File Name:demo-lseek.c
#	> Author: jarven
#	> Mail: whuaw@aliyun.com
#	> Created Time: 2022年11月18日 星期五 21时51分48秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <assert.h>

int main() {
	// 打印文件内容到STDOUT_FILENO
	char filename[] = "./testfile.tmp";
	remove(filename);

	int fd = open(filename, O_RDWR | O_CREAT, 0664);
	if(fd == -1) {
		perror("创建文件失败");
		exit(1);
	}

	char filecontent[] = "Hello world!\n";
	int result = write(fd, filecontent, strlen(filecontent));
	if(result == -1) {
		perror("写文件错误");
		exit(1);
	}

	lseek(fd, 0, SEEK_SET);
	
	char filecontentread[1024];
	ssize_t byteCount = read(fd, filecontentread, 1024);
	write(STDOUT_FILENO, filecontentread, byteCount); 

	// 获取文件大小
	off_t fileLength = lseek(fd, 0, SEEK_END);
	assert(strlen(filecontent) == fileLength);	

	// 扩展文件大小
	lseek(fd, 99, SEEK_END);
	write(fd, "\0", 1);
	fileLength = lseek(fd, 0, SEEK_END);
	assert(strlen(filecontent) + 100 == fileLength);

	close(fd);

	return 0;
}
