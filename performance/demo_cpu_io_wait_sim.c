// todo 退出程序时卡死
// 演示cpu io等待

// 编译
// gcc demo_cpu_io_wait_sim.c -o demo_cpu_io_wait_sim -lpthread

// 执行
// ./demo_cpu_io_wait_sim

#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>

FILE *file;
int total = 1024*1024;
int block = 1024;
int stop = 0;

void *myfunc(void *args)
{
	int length = total*block;
	while(!stop)
	{
		int randInt = rand();
		if(randInt < 0)
			randInt = -randInt;
		int randPosition = randInt%length;
		fseek(file, randPosition, SEEK_SET);
	

		randInt = rand();
		if(randInt < 0)
			randInt = -randInt;	
		int randSize = randInt%block;
		if(randSize <= 0)
			continue;

		char ch[randSize];
        	int lengthTemp = sizeof(ch)/sizeof(ch[0]);
        	int i=0;
        	for(i=0; i<lengthTemp; i++)
        	{
         		ch[i] = rand();
        	}
        	fwrite(ch, sizeof(ch), 1, file);

	   	fread(ch, sizeof(ch), 1, file);		
	}
	char *threadname = (char *)args;
	printf("%s结束\n", threadname);
}

int main()
{
	// 创建1g文件
	printf("正在创建1g文件，稍候。。。\n");
	srand(time(NULL));

        // 创建一个1g空白随机字节数组的文件
        file = fopen(".file.dat", "w+");
        if(file == NULL) {
                printf("无法创建文件 .file.dat");
                exit(1);
        }

        int j=0;
        for(j=0; j<total; j++)
        {
                char ch[block];
                int length = sizeof(ch)/sizeof(ch[0]);
                int i=0;
                for(i=0; i<length; i++)
                {
                        ch[i] = rand();
                }
                fwrite(ch, sizeof(ch), 1, file);
        }

        rewind(file);

	// 启动10条线程随机读写
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

	sleep(5);
	fclose(file);

	return 0;
}
