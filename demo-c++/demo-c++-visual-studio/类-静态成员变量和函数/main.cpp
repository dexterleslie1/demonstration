#include <iostream>

using namespace std;

class Person {
public:
	static int a1;

	static void setA2(int a2) {
		Person::a2 = a2;
	}

	static int getA2() {
		return Person::a2;
	}
private:
	static int a2;
};

// 类内声明静态成员变量，类外初始化静态成员变量
// 否则无法使用静态成员变量
int Person::a1 = 100;
int Person::a2 = 101;

int main() {
	// 使用对象访问类静态成员变量
	Person person;
	cout << "person.a1=" << person.a1 << endl;

	// 使用类访问静态成员变量
	Person::a1 = 101;
	cout << "被Person::a1修改后的person.a1=" << person.a1 << endl;

	// 无法直接访问私有静态成员变量
	// cout << person.a2 << endl;
	// cout << Person::a2 << endl;

	// 使用静态成员函数访问私有静态成员变量
	person.setA2(102);
	cout << "person.getA2()=" << person.getA2() << endl;
	Person::setA2(103);
	cout << "Person::getA2()=" << Person::getA2() << endl;

	system("pause");
	return 0;
}