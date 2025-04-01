/*************************************************************************
#	> File Name:demo-file-dir-ls.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月19日 星期六 21时49分12秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>

// 使用opendir、readdir、closedir实现类似lsi命令
int main() {
	DIR *dirp = opendir(".");
	if(dirp == NULL) {
		perror("打开目录失败");
		exit(1);
	}

	struct dirent *direntp;
	while((direntp = readdir(dirp)) != NULL) {
		printf("%s\t", direntp->d_name);
	}
	printf("\n");

	closedir(dirp);

	return 0;
}
