/*************************************************************************
#	> File Name:demo-typedef.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月28日 星期一 21时04分43秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

// 测试函数类型的变量
// https://blog.csdn.net/xiaorenwuzyh/article/details/48997767

// func_t是函数类型
typedef void func_t(int);
// func_ptr_1_t和func_ptr_t是函数类型指针
typedef void (*func_ptr_1_t)(int);
typedef func_t *func_ptr_t;

void my_test_fun(int a) {
    printf("argument a value %d\n", a);
}
void my_test_fun_proxy_0(int a, func_t funcPtr/* 函数类型形参实质为函数类型指针 func_t *funcPtr */) {
    assert(funcPtr != NULL);
    funcPtr(a);
}
void my_test_fun_proxy_1(int a, func_t *funcPtr) {
    assert(funcPtr != NULL);
    funcPtr(a);
}
void my_test_fun_proxy_2(int a, func_ptr_t funcPtr) {
    assert( funcPtr != NULL);
    funcPtr(a);
}
void my_test_fun_proxy_3(int a, func_ptr_1_t funcPtr) {
    assert( funcPtr != NULL);
    funcPtr(a);
}
void test_typedef_function() {
    my_test_fun_proxy_0(0, my_test_fun);
    my_test_fun_proxy_1(1, my_test_fun);

    my_test_fun_proxy_2(2, my_test_fun);
    func_ptr_t funcPtr = my_test_fun;
    my_test_fun_proxy_2(21, funcPtr);
    func_t *funcPtr1 = my_test_fun;
    my_test_fun_proxy_2(22, funcPtr1);

    my_test_fun_proxy_3(3, my_test_fun);
}

int main() {
    test_typedef_function();

	return 0;
}
