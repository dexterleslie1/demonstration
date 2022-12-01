/*************************************************************************
#	> File Name:demo-pthread-mutex-condition.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月28日 星期一 17时42分45秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>

// http://c.biancheng.net/view/8633.html
pthread_mutex_t mutex;
pthread_cond_t cond;
int GlobalCounter = 0;

void* routine_producer(void *args) {
	for(int i=0; i<10; i++)  {
		int result = pthread_mutex_lock(&mutex);
		if(result != 0) {
			perror("pthread_mutex_lock error");
			exit(1);
		}

		GlobalCounter++;
		// printf("producer increase GLobalCounter by a step GlobalCounter value %d\n", GlobalCounter);
	
		result = pthread_mutex_unlock(&mutex);
		if(result != 0) {
			perror("pthread_mutex_unlock error");
			exit(1);
		}

		result = pthread_cond_signal(&cond);
		if(result != 0) {
			perror("pthread_cond_signal error");
			exit(1);
		}

		sleep(1);
	}
}
void* routine_consumer(void *args) {
	int counter = 0;
	while(counter < 10) {
		int result = pthread_mutex_lock(&mutex);
		if(result != 0) {
			perror("pthread_mutex_lock error");
			exit(1);
		}

		if(GlobalCounter == 0) {
			// NOTE: pthread_cond_wait自动pthread_mutex_unlock并进入阻塞状态，
			// 当收到唤醒信号后，再次自动调用pthread_mutex_lock并取消阻塞
			// 所以pthread_cond_wait函数返回之后需要调用pthread_mutex_unlock解锁
			result = pthread_cond_wait(&cond, &mutex);
			if(result != 0) {
				perror("pthread_cond_wait error");
				exit(1);
			}
		}
		printf("GlobalCounter value %d\n", GlobalCounter);
		
		GlobalCounter--;
		
		result = pthread_mutex_unlock(&mutex);
		if(result != 0) {
			perror("pthread_mutex_unlock error");
			exit(1);
		}
		
		counter++;
	}
}
void test_condition() {
	pthread_t tid1, tid2, tid3;

	int result = pthread_mutex_init(&mutex, NULL);
	if(result != 0) {
		perror("pthread_mutex_init error");
		exit(1);
	}

	result = pthread_cond_init(&cond, NULL);
	if(result != 0) {
		perror("pthread_cond_init error");
		exit(1);
	}

	result = pthread_create(&tid1, NULL, routine_consumer, "1");
	if(result != 0) {
		perror("pthread_create error");
		exit(1);
	}
	/*result = pthread_create(&tid2, NULL, routine_consumer, "2");
	if(result != 0) {
		perror("pthread_create error");
		exit(1);
	}*/
	result = pthread_create(&tid3, NULL, routine_producer, NULL);
	if(result != 0) {
		perror("pthread_create error");
		exit(1);
	}

	result = pthread_join(tid1, NULL);
	if(result != 0) {
		perror("pthread_join error");
		exit(1);
	}
	/*result = pthread_join(tid2, NULL);
	if(result != 0) {
		perror("pthread_join error");
		exit(1);
	}*/

	result = pthread_cond_destroy(&cond);
	if(result != 0) {
		perror("pthread_cond_destroy error");
		exit(1);
	}

	result = pthread_mutex_destroy(&mutex);
	if(result != 0) {
		perror("pthread_mutex_destroy error");
		exit(1);
	}
}

int main() {
	test_condition();
	
	return 0;
}
