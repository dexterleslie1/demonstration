/*************************************************************************
#	> File Name:main.c
#	> Author: dexterleslie
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 日 11/13 00:43:50 2022
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "mylib.h"

int main(int argc, char *argv[]) {
	int a = 11;
	int b = 10;
	int c = add(a, b);
	printf("%d + %d = %d\n", a, b, c);

	c = sub(a, b);
	printf("%d - %d = %d\n", a, b, c);

	return 0;
}
