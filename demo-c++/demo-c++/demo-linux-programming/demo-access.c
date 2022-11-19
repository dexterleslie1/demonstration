/*************************************************************************
#	> File Name:demo-access.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月19日 星期六 20时46分06秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <assert.h>
#include <fcntl.h>

int main() {
	char filename[] = "./testfile.tmp";
	remove(filename);

	int result = access(filename, F_OK);
	assert(result == -1);

	int fd = open(filename, O_CREAT, 0664);
	if(fd == -1) {
		perror("创建文件失败");
		exit(1);
	}

	result = access(filename, F_OK);
	assert(result == 0);

	return 0;
}
