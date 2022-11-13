/*************************************************************************
#	> File Name:gdbdebug.c
#	> Author: dexterleslie
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 日 11/13 16:29:28 2022
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* 文件用于gdb调试辅助使用 */

int add(int a, int b) {
	int c = a + b;
	return c;
}

int main() {
	int a = 11;
	int b = 10;
	int c = add(a, b);
	printf("%d + %d = %d\n", a, b, c);	

	return 0;
}
