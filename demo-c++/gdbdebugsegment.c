/*************************************************************************
#	> File Name:gdbdebugsegment.c
#	> Author: jarven
#	> Mail: whuaw@aliyun.com
#	> Created Time: 2022年11月13日 星期日 17时20分10秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* 这个程序用于演示使用gdb找出段错误 */

int main(int argc, char *argv[]) {
	printf("Hello world\n");

	char *ch = "Hello world";
	// 段错误位置
	ch[0] = 'M';

	return 0;
}
