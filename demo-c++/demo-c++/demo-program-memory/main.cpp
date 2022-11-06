#include <iostream>
#include <string>

using namespace std;

// 全局变量
int g_a = 10;
int g_b = 11;

// const修饰全局常量
const int c_g_a = 10;

void test(int p_a) {
	cout << "函数参数p_a地址：" << &p_a << endl;
}

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

	system("pause");

	return 0;
}