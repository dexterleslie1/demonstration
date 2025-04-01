#include <iostream>

using namespace std;

class Person {
public:
	void function1(int b) const {
		// 常函数内不能修改成员变量值
		// this->a = 100;

		// 常函数内能够修改mutable修饰的成员变量值
		this->b = b;
	}

	void function2() {

	}

	int a;
	mutable int b;
};

int main() {
	Person person1;
	person1.function1(101);
	cout << "person1.b=" << person1.b << endl;

	// 常对象只能调用常函数
	const Person person2;
	//person2.function2();
	person2.function1(102);
	cout << "person2.b=" << person2.b << endl;

	system("pause");
	return 0;
}