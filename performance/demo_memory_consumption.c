// 演示内存消耗

// 编译
// gcc demo_memory_consumption.c -o demo_memory_consumption

// 运行
// ./demo_memory_consumption

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main()
{
        while(1)
        {
                void *m = malloc(1024*1024);
                memset(m,0,1024*1024);
		usleep(10000);
        }
        return 0;
}
