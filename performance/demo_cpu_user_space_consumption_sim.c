// 用于演示用户空间消耗cpu

// 编译
// gcc demo_cpu_user_space_consumption_sim.c -o demo_cpu_user_space_consumption_sim -lpthread

// 运行
// ./demo_cpu_user_space_consumption_sim

#include <stdio.h>
#include <pthread.h>

void *myfunc() {
        while(1);
}

int main() {
        int i=1;
        for(i=1; i<=10; i++) {
                pthread_t th;
                pthread_create(&th, NULL, myfunc, NULL);
                printf("启动第%d个线程\n", i);
        }

        while(1);
        return 0;
}
