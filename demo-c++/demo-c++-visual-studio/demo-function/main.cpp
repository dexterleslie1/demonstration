#include <string>
#include "my_function.h"

using namespace std;

void test(int a, int b = 10) {
	cout << "a=" << a << ",b=" << b << endl;
}

void test1(int a, int = 10) {
	cout << "test1 a=" << a << endl;
}

void test2(int a) {
	cout << "a=" << a << endl;
}
void test2(double a) {
	cout << "a=" << a << endl;
}
void test2(int a, int b) {
	cout << "a=" << a << ",b=" << b << endl;
}
void test2(int a, double b) {
	cout << "a=" << a << ",b=" << b << endl;
}
void test2(double a, int b) {
	cout << "a=" << a << ",b=" << b << endl;
}

int main() {
	// 函数默认参数
	int a = 10;
	test(a);
	int b = 20;
	test(a, b);

	// 函数占位参数
	test1(a);

	// 函数重载 overload
	// 参数类型不同
	test2(a);
	double d = 3.14;
	test2(d);
	// 参数个数不一样
	test2(a, b);
	// 参数顺序不一样
	test2(a, d);
	test2(d, a);

	// 测试函数声明和定义分开文件，声明使用头文件h，定义使用cpp文件
	cout << "-------测试函数声明和定义分开文件" << endl;
	my_function(a);

	system("pause");

	return 0;
}