#include <iostream>

using namespace std;

// 使用typedef定义函数类型，用于生命函数类型变量和函数类型指针
typedef void my_fun_t(int a);
typedef my_fun_t *my_fun_p_t;

void fun1(int a) {
	cout << "fun1输出：" << a << endl;
}
void fun2(int b) {
	cout << "fun2输出：" << b << endl;
}

int main() {
	/* 指针定义和使用 */
	int a = 10;
	int *p = &a;
	cout << "*p=" << *p << endl;

	/* 指针占用内存空间，32位操作指针占用4字节，64位操作系统指针占用8字节 */
	cout << "指针占用内存：" << sizeof(p) << endl;

	/* 空指针用于初始化指针指向内存地址位0x00000的内存单元，不能用空指针操作内存 */
	int *p1 = NULL;
	// cout << "不能使用空指针操作内存" << *p1 << endl;

	/* 野指针 */
	int *p2 = (int *)0x89977;
	// cout << "不能使用野指针操作内存" << *p2 << endl;

	/* const修饰指针 */
	// 常量指针，指针可以修改，指针指向值不可以修改
	int b = 20;
	const int *p3 = &b;
	//*p3 = 20;
	p3 = &a;
	cout << "*p3=" << *p3 << endl;

	// 指针常量，指针不可以修改，指针指向值可以修改
	int * const p5 = &b;
	*p5 = 20;
	//p5 = &a;
	cout << "*p5=" << *p5 << endl;

	// 即修饰指针有修饰常量，指针和指针指向值都不可以修改
	const int * const p6 = &a;
	//*p6 = 20;
	//p6 = &b;
	cout << "*p6=" << *p6 << endl;

	/* 指针和数组 */
	int arr1[] = {1, 2, 3, 5};
	int *p7 = arr1;
	cout << "arr1=" << arr1 << ",p7=" << p7 << endl;
	cout << "*p7=" << *p7 << endl;

	cout << "使用指针遍历数组: ";
	for(int i=0; i<sizeof(arr1) / sizeof(int); i++) {
		cout << "arr1[" << i << "]=" << *p7 << ",";
		p7++;
	}
	cout << endl;

	/* 
		函数类型和函数指针
		https://blog.csdn.net/xiaorenwuzyh/article/details/48997767
	*/
	my_fun_t *myFunP1 = fun1;
	myFunP1(10);
	my_fun_t *myFunP2 = fun2;
	myFunP2(11);
	// 使用函数指针数据类型声明函数指针变量
	my_fun_p_t myFunP11 = fun1;
	myFunP11(12);
	my_fun_p_t myFunP21 = fun2;
	myFunP21(13);

	system("pause");

	return 0;
}