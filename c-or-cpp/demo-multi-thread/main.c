#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>

int stop = 0;

void *myfunc(void *arg) {
        char *threadname = (char *)arg;
        while(!stop);

        // char *threadname = (char *)arg;
        printf("%s退出\n", threadname);
}

int main() {
        int i=1;
        for(i=1; i<=10; i++) {
                pthread_t th;
                char *threadname = (char *) malloc(strlen("线程") + sizeof(int));
                sprintf(threadname, "线程%d", i);
                pthread_create(&th, NULL, myfunc, threadname);
                printf("启动第%d个线程\n", i);
        }

        printf("输入任意键退出");
        getchar();

        stop = 1;

        sleep(1);

        return 0;
}
