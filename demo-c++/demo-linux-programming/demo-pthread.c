/*************************************************************************
#	> File Name:demo-pthread.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月27日 星期日 20时51分32秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>

// 编译 gcc demo-pthread.c -l pthread -o test

// 测试pthread_self函数
void test_pthread_self() {
	printf("process id %d, pthread id %ld\n", getpid(), pthread_self());
}

void *routine_pthread(void *args) {
	printf("thread id %ld\n", pthread_self());
}

// 测试pthread_create函数
void test_pthread_create() {
	pthread_t tid;
	int result = pthread_create(&tid, NULL, routine_pthread, NULL);
	if(result != 0) {
		perror("pthread_create error");
		exit(1);
	}

	sleep(1);	
}

// 测试创建多个线程
void test_create_multiple_threads() {
	for(int i=0; i<5; i++) {
		pthread_t tid;
		int result = pthread_create(&tid, NULL, routine_pthread, NULL);
		if(result != 0) {
			perror("pthread_create error");
			exit(1);
		}
	}

	sleep(1);
}

// 线程间共享全局变量
int globalV = 0;
void *routine_global_variable(void *args) {
	globalV = 100;
}
void test_thread_share_global_variable() {
	pthread_t tid;
	int result = pthread_create(&tid, NULL, routine_global_variable, NULL);
	if(result != 0) {
		perror("pthread_create error");
		exit(1);
	}
	
	sleep(1);
	printf("main thread global variable %d\n", globalV);
}

// 使用pthread_exit退出线程执行
void *routine_pthread_exit(void *args) {
	pthread_exit(NULL);
	printf("this line can never be printed\n");
}
void test_thread_exit() {
	pthread_t tid;
	int result = pthread_create(&tid, NULL, routine_pthread_exit, NULL);
	if(result != 0) {
		perror("pthread_create error");
		exit(1);
	}

	sleep(1);
}

// thread_join等待线程结束
void *routine_thread_join() {
	sleep(3);
	printf("routine thread join\n");
}
void test_thread_join() {
	pthread_t tid;
	int result = pthread_create(&tid, NULL, routine_thread_join, NULL);
	if(result != 0) {
		perror("pthread_create error");
		exit(1);
	}

	pthread_join(tid, NULL);
}

// thread_cancel取消线程执行
void *routine_thread_cancel() {
	sleep(3);
	printf("this line can never be printed\n");
}
void test_thread_cancel() {
	pthread_t tid;
	int result = pthread_create(&tid, NULL, routine_thread_cancel, NULL);
	if(result != 0) {
		perror("pthread_create error");
		exit(1);
	}

	sleep(1);
	pthread_cancel(tid);
	printf("thread id %ld has been canceled by using pthread_cancel\n", tid);
}

int main() {
	// test_pthread_self();
	// test_pthread_create();
	// test_create_multiple_threads();
	// test_thread_share_global_variable();
	// test_thread_exit();
	// test_thread_join();
	test_thread_cancel();

	return 0;
}
