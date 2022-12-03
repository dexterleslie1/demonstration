#include <iostream>

using namespace std;

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

	/* 
		const修饰指针 
		https://blog.csdn.net/weixin_52244492/article/details/124081709
	*/
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

	// 即修饰指针又修饰常量，指针和指针指向值都不可以修改
	const int * const p6 = &a;
	//*p6 = 20;
	//p6 = &b;
	cout << "*p6=" << *p6 << endl;

	system("pause");

	return 0;
}