/*
 * demo-int-pointer.c — 指向整型（int）的指针基础示例
 *
 * 核心概念：
 *   - int *p   ：p 的类型是「指向 int 的指针」，p 里存的是某个 int 的地址
 *   - &x        ：取变量 x 的地址（类型为 int *）
 *   - *p        ：解引用，通过地址访问/修改该地址上的 int
 *   - int *f()  ：返回 int * 的函数（参考 __errno_location），通过 *f() 读写共享的 int
 *   - #define E (*f()) ：宏展开后像整型左值，与直接写 *f() 等价（见 DEMO_ERRNO）
 */

#include <stdio.h>
#include <stddef.h>

/*
 * 参考 glibc 中「返回指向 int 的指针」的典型声明（Linux 上 errno 由此访问）：
 *
 *   extern int *__errno_location(void);
 *
 * 含义：函数名后的 (void) 表示无参数；返回类型是 int *，即「指向 int 的指针」。
 * 库在内部持有某个 int（如每线程一份 errno），把它的地址返回给调用方；
 * 调用方不接收「整型副本」，而是拿到地址，通过 *ptr 与库共享同一份 int。
 *
 * 下面用静态变量模拟那份存储，函数行为与「取 errno 地址」一致，便于本地理解。
 */
static int g_demo_errno_storage;

static int *
demo_errno_location(void)
{
    return &g_demo_errno_storage;
}

/*
 * 参考 glibc（Linux）中 errno 的常见定义方式：
 *
 *   #define errno (*__errno_location())
 *
 * 预处理时把标识符 errno 替换成 (*__errno_location())，因此：
 *   - 写 errno = n  展开为  *__errno_location() = n
 *   - 读 errno       展开为  *__errno_location()
 * 表面像「整型变量」，实际每次通过函数取到当前线程（或库内部）那份 int 的地址再解引用。
 *
 * 下面 DEMO_ERRNO 用本文件的 demo_errno_location 做同样展开，便于对照理解。
 */
#define DEMO_ERRNO (*demo_errno_location())

int main(void)
{
    int x = 42;

    /* int * 表示「指向 int 的指针」；未初始化前不要解引用 */
    int *p = &x;

    /*
     * &x：一元运算符，得到 x 在内存中的地址，类型为 int *。
     * 把该地址赋给 p 后，p 与 x 指向同一块 int 存储。
     */

    printf("x = %d\n", x);
    printf("p 指向的值 *p = %d\n", *p);
    printf("p 中保存的地址（用 %%p 打印）: %p\n", (void *)p);

    /* 通过指针修改目标整型：*p 与 x 是同一对象 */
    *p = 100;
    printf("经 *p 赋值后 x = %d\n", x);

    /*
     * 指针本身也有地址：&p 的类型是 int **（指向「指向 int 的指针」的指针）
     * 这里仅演示二级指针的声明与一次解引用、二次解引用。
     */
    int **pp = &p;
    printf("**pp = %d（与 x、*p 相同）\n", **pp);

    /*
     * NULL：空指针常量，表示「不指向任何有效对象」。
     * 对 NULL 解引用是未定义行为，使用前必须判空。
     */
    int *q = NULL;
    if (q == NULL) {
        printf("q 为空指针，不能 *q\n");
    }

    /*
     * sizeof：这里 p 是 int *，sizeof(p) 为指针变量本身的大小（与 int 大小无关）；
     * sizeof(*p) 等价于 sizeof(int)，是被指向类型的大小。
     */
    printf("sizeof(p) = %zu（指针宽度）, sizeof(*p) = %zu（int 宽度）\n",
           sizeof p, sizeof *p);

    /*
     * 函数返回值类型为 int * 时的用法（与 *errno = … / 读 errno 同理）：
     *
     * 1) 把返回值存进 int * 变量，再解引用，意图最清晰。
     * 2) 也可直接对「函数调用表达式」解引用：*demo_errno_location() = 值
     *    因函数返回的就是指针，等价于先赋给临时 int * 再 * 赋值。
     * 3) DEMO_ERRNO 宏展开为 (*demo_errno_location())，与 2) 等价。
     */
    int *errptr = demo_errno_location();
    *errptr = -2;
    printf("经 *errptr 设置后 g_demo_errno_storage = %d\n", g_demo_errno_storage);

    *demo_errno_location() = 0; /* 链式写法：返回 int *，左侧整体是可修改的 *int */
    printf("经 *demo_errno_location() 写入后 = %d\n", g_demo_errno_storage);

    /*
     * #define DEMO_ERRNO (*demo_errno_location()) 展开后，读写 DEMO_ERRNO 与上面 *demo_errno_location() 相同。
     */
    DEMO_ERRNO = 5;
    printf("DEMO_ERRNO = 5 后 g_demo_errno_storage = %d，读取 DEMO_ERRNO = %d\n",
           g_demo_errno_storage, DEMO_ERRNO);

    return 0;
}
