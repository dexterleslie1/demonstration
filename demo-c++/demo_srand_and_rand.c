#include <stdio.h>
#include <time.h>

// 参考资料
// http://c.biancheng.net/view/2043.html
// https://stackoverflow.com/questions/15621764/generate-a-random-byte-stream

// 编译
// gcc demo_srand_and_rand.c -o demo_srand_and_rand

int main()
{
        srand(time(NULL));

        printf("使用rand函数获取的10个随机数如下：\n");
        int i=1;
        for(i=1; i<=10; i++)
        {
                int randInt = rand();
                printf("randInt%d=%d\n", i, randInt);
        }

        // 生成指定范围的随机数
        int randInt = rand();
        randInt = randInt%51 + 13;
        printf("范围在13-63之间的随机数为：%d\n", randInt);

        char randByte = rand();
        printf("使用rand函数生成的随机byte数为：%d\n", randByte);

        return 0;
}
