/*************************************************************************
#	> File Name:demo-link-unlink.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月19日 星期六 20时37分09秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <assert.h>

int main() {
	char filename[] = "./testlinkunlink.tmp";
	remove(filename);
	
	int fd = open(filename, O_CREAT | O_RDWR, 0664);
	if(fd == -1) {
		perror("创建文件失败");
		exit(1);
	}

	// link创建硬链接
	char filenamelink[] = "./testfilelink.tmp";
	remove(filenamelink);
	int result = link(filename, filenamelink);
	if(result == -1) {
		perror("创建硬链接失败");
		exit(1);
	}

	// unlink测试，unlink之后不会马上删除硬链接
	// 直到程序退出后才自动删除文件
	// 并且在文件未删除之前依旧能够对文件读写操作
	int fd2 = open(filenamelink, O_RDWR);
	if(fd2 == -1) {
		perror("打开文件失败");
		exit(1);
	}

	result = unlink(filenamelink);
	if(result == -1) {
		perror("unlink文件错误");
		exit(1);
	}
	
	char filecontent[] = "hello world!";
	ssize_t writeCount = write(fd2, filecontent, strlen(filecontent));
	if(writeCount == -1) {
		perror("写文件失败");
		exit(1);
	}

	char filecontentread[1024];
	ssize_t readCount = read(fd, filecontentread, 1024);
	assert(writeCount == readCount);
	for(int i = 0; i < writeCount; i++) {
		assert(filecontent[i] == filecontentread[i]);	
	}

	close(fd);	
	close(fd2);

	return 0;
}
