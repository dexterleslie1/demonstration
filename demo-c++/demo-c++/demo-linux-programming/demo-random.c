/*************************************************************************
#	> File Name:demo-random.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月28日 星期一 10时56分49秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <time.h>

// https://blog.csdn.net/owww_zpl/article/details/126292349
int main() {
	printf("RAND_MAX is %d\n", RAND_MAX);

	// NOTE：没有调用srand设置seed，rand返回序列每次都相同
	for(int i=0; i<5; i++) {
		int randomInt = rand();
		printf("random int is %d\n", randomInt);
		sleep(0.1);
	}

	// 调用srand设置seed
	srand(time(NULL));
	for(int i=0; i<5; i++) {
		int randomInt = rand();
		printf("random int %d after srand seed\n", randomInt);
		sleep(0.1);
	}

	// generate random int number from within a specific range
	int min = 3;
	int max = 6;
	for(int i=0; i<5; i++) {
		int randomInt = rand() % (max+1-min) + min;  
		printf("random int %d is between [%d, %d]\n", randomInt, min, max);
	}

	return 0;
}
