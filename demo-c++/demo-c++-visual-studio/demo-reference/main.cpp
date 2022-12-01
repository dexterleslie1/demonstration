#include <iostream>
#include <string>

using namespace std;

void swap(int &a, int &b) {
	int temp = a;
	a = b;
	b = temp;
}

int& test(int &a) {
	return a;
}

void test1(const int &a) {
	//a = 10; 不允许修改
	cout << "a=" << a << endl;
}

int main() {
	// 引用的本质是指针常量

	// 引用基本用法
	int a = 10;
	int &b = a;
	cout << "a=" << a << endl;
	cout << "b=" << b << endl;

	// 引用作为函数参数
	int a1 = 10;
	int b1 = 20;
	swap(a1, b1);
	cout << "a1=" << a1 << ",b1=" << b1 << endl;

	// 引用作为函数返回值
	int a2 = 10;
	int &b3 = test(a2);
	cout << "修改前 a2=" << a2 << ",b3=" << b3 << endl;
	b3 = 1000;
	cout << "修改后 a2=" << a2 << ",b3=" << b3 << endl;

	// 常量引用，防止参数被修改
	int a3 = 10;
	test1(a3);

	system("pause");

	return 0;
}