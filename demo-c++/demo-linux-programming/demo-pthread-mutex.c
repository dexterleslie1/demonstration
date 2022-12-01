/*************************************************************************
#	> File Name:demo-pthread-mutex.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月28日 星期一 13时01分13秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <time.h>
#include <unistd.h>

#define RANGE_MIN 1
#define RANGE_MAX 2

// mutex使用
int globalCounter = 0;
pthread_mutex_t mutex;
void* routine_mutex(void *args) {
	for(int i=0; i<1000000; i++) {
		pthread_mutex_lock(&mutex);

		globalCounter++;

		pthread_mutex_unlock(&mutex);
	}
}
void test_mutex() {
	int result = pthread_mutex_init(&mutex, NULL);
	if(result != 0) {
		perror("pthread_mutex_init error");
		exit(1);
	}

	pthread_t tid, tid2;
	result = pthread_create(&tid, NULL, routine_mutex, NULL);
	if(result != 0) {
		perror("pthread_create error");
		exit(1);
	}

	result = pthread_create(&tid2, NULL, routine_mutex, NULL);
	if(result != 0) {
		perror("pthread_create error");
		exit(1);
	}

	result = pthread_join(tid, NULL);
	if(result != 0) {
		perror("pthread_join error");
		exit(1);
	}

	result = pthread_join(tid2, NULL);
	if(result != 0) {
		perror("pthread_join error");
		exit(1);
	}

	result = pthread_mutex_destroy(&mutex);
	if(result != 0) {
		perror("pthread_mutex_destroy error");
		exit(1);
	}	

	printf("global counter value is %d\n", globalCounter);
}

int main() {
	test_mutex();		

	return 0;
}
