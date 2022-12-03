#include <iostream>
#include <string>
#include <stdio.h>

using namespace std;

void swap(int &a, int &b) {
	int temp = a;
	a = b;
	b = temp;
}

void swap(int *a, int *b) {
	int temp = *a;
	*a = *b;
	*b = temp;
}

int& test(int &a) {
	return a;
}

void test1(const int &a) {
	//a = 10; 不允许修改
	cout << "a=" << a << endl;
}

// 不能返回局部变量的引用
int& test2(){
	int a = 10;
	return a;
}

int main() {
	// 给变量起别名

	// 引用基本用法
	int a = 10;
	// 下面用法是错误的，引用变量必须定义时初始化
	// int &b;
	// 使用变量原名赋给变量别名
	int &b = a;
	cout << "a=" << a << endl;
	cout << "b=" << b << endl;

	// 引用作为函数参数
	int a1 = 10;
	int b1 = 20;
	swap(a1, b1);
	cout << "使用引用传递参数swap a1=" << a1 << ",b1=" << b1 << endl;

	a1 = 10;
	b1 = 20;
	swap(&a1, &b1);
	cout << "使用地址传递参数swap a1=" << a1 << ",b1=" << b1 << endl;

	// 引用作为函数返回值
	int a2 = 10;
	int &b3 = test(a2);
	cout << "修改前 a2=" << a2 << ",b3=" << b3 << endl;
	b3 = 1000;
	cout << "修改后 a2=" << a2 << ",b3=" << b3 << endl;

	// 返回类型为引用返回的函数调用可以作为左值直接修改
	a2 = 12;
	cout << "返回类型为引用返回的函数调用左值前a2=" << a2 << endl;
	test(a2) = 100;
	cout << "返回类型为引用返回的函数调用左值后a2=" << a2 << endl;

	// 常量引用，防止形参被修改
	int a3 = 10;
	test1(a3);

	// 不能返回局部变量的引用
	int &a11 = test2();
	cout << "返回函数局部变量引用值：" << a11 << endl;
	// 这个引用输出被回收的局部变量导致a11值不确定
	cout << "返回函数局部变量引用值2：" << a11 << endl;

	// 引用的本质是指针常量
	int a12 = 10;
	// 编译器会自动转换为int *const refA12 = &a12
	int &refA12 = a12;
	cout << "refA12=" << refA12 << endl;

	system("pause");

	return 0;
}