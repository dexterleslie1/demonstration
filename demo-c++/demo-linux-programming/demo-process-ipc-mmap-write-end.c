/*************************************************************************
#	> File Name:demo-process-ipc-mmap-write-end.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月24日 星期四 23时20分12秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/mman.h>

int main() {
	char *filename = "./test-ipc-mmap.tmp";
	int fd = open(filename, O_RDWR | O_CREAT | O_TRUNC, 0644);
	if(fd == -1) {
		perror("open error");
		exit(1);
	}

	ftruncate(fd, 1024);

	off_t size = lseek(fd, 0, SEEK_END);

	char *mmapP = (char *)mmap(NULL, size, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
	if(mmapP  == MAP_FAILED) {
		perror("mmap error");
		exit(1);
	}

	close(fd);

	for(int i=0; i<10; i++) {
		char content[1024];
		sprintf(content, "内容%d\n", i);
		printf("写内容到mmap：%s", content);
		strcpy(mmapP, content);
		sleep(1);
	}

	int result = munmap(mmapP, size);
	if(result == -1) {
		perror("munmap error");
		exit(1);
	}

	return 0;
}
