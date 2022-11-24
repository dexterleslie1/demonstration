/*************************************************************************
#	> File Name:demo-process-ipc-mmap-read-end.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月24日 星期四 23时35分46秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/mman.h>

int main() {
	char *filename = "./test-ipc-mmap.tmp";
	int fd  = open(filename, O_RDONLY);
	if(fd ==  -1) {
		perror("open error");
		exit(1);
	}

	off_t size = lseek(fd, 0, SEEK_END);

	char *mmapP = (char *)mmap(NULL, size, PROT_READ, MAP_SHARED, fd, 0);
	if(mmapP == MAP_FAILED) {
		perror("mmap error");
		exit(1);
	}

	close(fd);

	for(int i=0; i<10; i++) {
		printf("读取mmap内容：%s", mmapP);
		sleep(1);
	}

	int result = munmap(mmapP, size);
	if(result == -1) {
		perror("munmap error");
		exit(1);
	}

	return 0;
}
