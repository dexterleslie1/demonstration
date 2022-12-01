/*************************************************************************
#	> File Name:demo-duplicate.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月19日 星期六 23时36分51秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <assert.h>
#include <unistd.h>

int main() {
	char filename[] = "./testduplicate.tmp";
	remove(filename);

	/* dup测试 */
	int oldfd = open(filename, O_CREAT | O_RDWR, 0664);
	int newfd = dup(oldfd);
	assert(newfd == oldfd + 1);
	
	// oldfd和newfd指向同一个文件
	char filecontent[] = "hello world!";
	int result = write(newfd, filecontent, strlen(filecontent));
	if(result == -1) {
		perror("写文件出错");
		exit(1);
	}

	close(oldfd);
	close(newfd);

	int writeFileLength = result;
	oldfd = open(filename, O_RDONLY);
	off_t fileSize = lseek(oldfd, 0, SEEK_END);
	assert(writeFileLength == fileSize);

	close(oldfd);

	truncate(filename, 0);

	/* dup2重定向测试 */
	// 文件重定向
	char filename2[] = "./testduplicate2.tmp";
	remove(filename2);
	
	int fd1 = open(filename, O_RDWR);
	int fd2 = open(filename2, O_CREAT, 0664);

	// fd2重定向到fd1，换句话说fd2复制fd1文件描述符信息，fd1和fd2指向相同的fd1对应的文件
	newfd = dup2(fd1, fd2);
	ssize_t writeLength = write(fd2, filecontent, strlen(filecontent));
	
	close(fd1);
	close(fd2);
	close(newfd);

	int fd = open(filename2, O_RDONLY);
	off_t fileLength = lseek(fd, 0, SEEK_END);
	close(fd);
	assert(fileLength == 0);

	fd = open(filename, O_RDONLY);
	fileLength = lseek(fd, 0, SEEK_END);
	close(fd);
	assert(fileLength == writeLength);

	truncate(filename, 0);

	// 恢复STDOUT_FILENO重定向
	// https://www.cnblogs.com/ngnetboy/p/3314804.html
	/* 复制标准输出描述符 */
    int s_fd = dup(STDOUT_FILENO);
    if (s_fd < 0) {
    	printf("err in dup\n");
    }

	// STDOUT_FILENO重定向
	fd = open(filename, O_RDWR);
	newfd = dup2(fd, STDOUT_FILENO);
	//printf("%s", filecontent);
	write(STDOUT_FILENO, filecontent, strlen(filecontent));
	close(fd);

	/* 重定向恢复标准输出 */
    if (dup2(s_fd, newfd) < 0) {
   		printf("err in dup2\n");
    }
	fd = open(filename, O_RDONLY);
	fileLength = lseek(fd, 0, SEEK_END);
	close(fd);
	assert(fileLength == strlen(filecontent));

	return 0;
}
