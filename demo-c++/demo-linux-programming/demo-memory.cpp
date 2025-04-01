//
// Created by dexterleslie on 22-11-28.
//
// 编译 g++ demo-memory.cpp -o test

#include <iostream>
#include <string.h>
#include <assert.h>

using namespace std;

// 全局变量
int g_a = 10;
int g_b = 11;

// const修饰全局常量
const int c_g_a = 10;

void test(int p_a) {
    cout << "函数参数p_a地址：" << &p_a << endl;
}

class ClassTest {
private:
    int mInt;
public:
    ClassTest(int mInt) {
        this->mInt = mInt;
    }

    void print() {
        cout << "ClassTest类实例mInt=" << this->mInt << endl;
    }
};

int main() {
    /* 程序内存分为：代码区、全局区、栈区、堆区 */
    // 代码区，程序编译链接后生成exe可执行程序包含全部代码区，存放cpu执行的机器指令
    // 代码区是共享的，同一个程序多个运行进程共享一份代码区，代码区是只读的，防止程序
    // 意外地修改指令

    // 全局区，存放全局变量、静态变量、字符串常量、const修饰全局常量
    cout << "全局变量g_a地址：" << &g_a << endl;
    cout << "全局变量g_b地址：" << &g_b << endl;

    static int s_a = 10;
    static int s_b = 11;
    cout << "静态变量s_a地址：" << &s_a << endl;
    cout << "静态变量s_b地址：" << &s_b << endl;

    cout << "字符串常量地址：" << &"Hello world" << endl;

    cout << "const修饰全局常量c_g_a地址：" << &c_g_a << endl;

    // 栈区（函数参数和局部变量），有编译器生成汇编代码时自动压栈和出栈
    int l_a = 10;
    int l_b = 11;
    cout << "局部变量l_a地址：" << &l_a << endl;
    cout << "局部变量l_b地址：" << &l_b << endl;

    test(l_a);

    // 堆区，由程序员分配和释放，若程序员不释放，程序结束时有操作系统回收
    int *heap_a = new int(10);
    int *heap_b = new int(11);
    cout << "堆区变量a地址：" << heap_a << endl;
    cout << "堆区变量b地址：" << heap_b << endl;

    /* 动态内存分配 */
    // https://blog.csdn.net/qq_53086187/article/details/124527767

    // 分配一个大小为size字节的内存块，返回该内存块开头的地址
    // 分配一个10个整型大小的内存块，将开头地址传递给p。
    // 如果malloc函数分配内存失败则会返回一个空指针。所以使用时需要进行判断。
    cout << endl << "---------------演示使用malloc分配内存" << endl;
    int byteCount = 2;
    int *p1 = (int *)malloc(byteCount * sizeof(int));
    cout << "malloc分配的内存地址：" << p1 << endl;
    if(p1 != NULL) {
        for(int i = 0; i < byteCount; i++) {
            *(p1 + i) = i;
        }

        for(int i = 0; i < byteCount; i++) {
            cout << "*(p1 + " << i << ")=" << *(p1 + i) << endl;
        }
    }

    cout << endl << "-----------------演示使用calloc分配内存" << endl;
    // 分配byteCount个int类型大小的内存空间
    // 指针指向的分配内存空间为 byteCount * sizeof(int)
    p1 = (int *)calloc(byteCount, sizeof(int));
    cout << "calloc分配的内存地址：" << p1 << endl;
    if(p1 != NULL) {
        for(int i = 0; i < byteCount; i++) {
            *(p1 + i) = i;
        }

        for(int i = 0; i < byteCount; i++) {
            cout << "*(p1 + " << i << ")=" << *(p1 + i) << endl;
        }
    }

    cout << endl << "---------------------演示使用free释放分配内存" << endl;
    // 通过动态内存分配的内存空间在不使用时要进行释放。
    free(p1);
    p1 = NULL;

    cout << endl << "-------------------演示使用realloc调整分配内存大小" << endl;
    // 当我们开辟一块空间后不能够满足需求时，可以使用realloc函数进行大小调整
    // 更改ptr指向的内存块的大小为size，如果ptr为空指针则相当于malloc开辟空间。
    // realloc函数调整空间时分为两种情况。
    // 第一：如果原空间后的内存空间足够分配，则直接进行空间追加。
    // 第二：如果原空间后的内存空间不足分配，则重新开辟一块空间，将开头地址返回。
    // 如果内存调整失败则会返回一个空指针。所以使用前需要进行判断。
    p1 = (int *)calloc(byteCount, sizeof(int));
    for(int i = 0; i < byteCount; i++) {
        *(p1 + i) = i;
    }
    for(int i = 0; i < byteCount; i++) {
        cout << "*(p1 + " << i << ")=" << *(p1 + i) << endl;
    }
    byteCount = byteCount * 2;
    int *p2 = (int *)realloc(p1, byteCount * sizeof(int));
    cout << "realloc分配的内存地址：" << p1 << endl;
    assert(p1 == p2);
    for(int i = 0; i < byteCount; i++) {
        cout << "*(p1 + " << i << ")=" << *(p1 + i) << endl;
    }

    /* 演示memcpy和memset */
    // https://blog.csdn.net/weixin_49713302/article/details/122542873

    // 演示memcpy
    // 用来将src地址处的内容拷贝n个字节的数据至目标地址dest指向的内存中去。
    // 返回值：函数返回指向dest的指针。
    cout << endl << "----------------演示memcpy用法" << endl;
    byteCount = 2;
    int *pSrc = (int *)calloc(byteCount, sizeof(int));
    for(int i = 0; i < byteCount; i++) {
        *(pSrc + i) = i;
    }
    int *pDest = (int *)calloc(byteCount, sizeof(int));
    pDest = (int *)memcpy(pDest, pSrc, byteCount * sizeof(int));
    for(int i = 0; i < byteCount; i++) {
        cout << "*(pDest + " << i << ")=" << *(pDest + i) << endl;
    }

    // 演示memset
    // 作用：将已开辟内存空间 s 的首 n 个字节的值设为值 c（给空间初始化）。这个函数通常为新申请的内存做初始化工作。
    cout << endl << "----------------演示memset用法" << endl;
    p1 = (int *)calloc(byteCount, sizeof(int));
    memset(p1, 0, byteCount * sizeof(int));
    for(int i = 0; i < byteCount; i++) {
        cout << "*(p1 + " << i << ")=" << *(p1 + i) << endl;
    }

    // 使用memset设置int变量值
    printf("--------------- int变量 memset 用法\n");
    int a = 1;
    printf("variable a before memset value is %d\n", a);
    memset(&a, 0, sizeof(a));
    printf("variable a after memset value is %d\n", a);
    printf("\n");

    // 使用memset设置 char * 内存
    printf("--------------- char*指针 memset 用法\n");
    char strArr[] = "hello world!";
    char *strPtr2 = (char *)calloc(sizeof(strArr), sizeof(char));
    strcpy(strPtr2, strArr);
    printf("memset前 %s\n", strPtr2);
    memset(strPtr2, 'a', sizeof(strArr));
    printf("memset后 %s\n", strPtr2);
    printf("\n");

    // 使用memcmp比较前n个字节
    char strArr2[] = "hello world!";
    const char *strPtr3 = "hello world";
    int result = memcmp(strArr2, strPtr3, sizeof(strArr2));
    assert(result > 0);

    const char *strPtr5 = "hfllo world";
    result = memcmp(strArr2, strPtr5, sizeof(strArr2));
    assert(result < 0);

    const char *strPtr6 = "hello world!";
    result = memcmp(strArr2, strPtr6, sizeof(strArr2));
    assert(result == 0);

    /* new和delete */
    // 使用new创建int等于10
    int *ptrInt = new int(10);
    cout << "使用new创建int等于10，*ptrInt=" << *ptrInt << endl;
    delete ptrInt;

    // 使用new创建int[2]数组
    ptrInt = new int[2];
    *ptrInt = 1;
    *(ptrInt+1) = 2;
    cout << "使用new创建int[2]，*ptrInt=" << *ptrInt << endl;
    cout << "使用new创建int[2]，*(ptrInt+1)=" << *(ptrInt+1) << endl;
    delete []ptrInt;

    // 使用new创建class
    // https://blog.csdn.net/qq_42138448/article/details/115304990
    ClassTest *classTest = new ClassTest(10);
    classTest->print();
    // 相当于显示的调用了该对象的析构函数
    delete classTest;

    return 0;
}