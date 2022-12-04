#include <iostream>

using namespace std;

class Person {
public:
	int age;

	Person():age(0) {
	}

	// 加号运算符重载
	Person operator+(const Person &person) {
		Person personTemp;
		personTemp.age = this->age + person.age;
		return personTemp;
	}

	Person operator+(const int age) {
		Person personTemp;
		personTemp.age = this->age + age;
		return personTemp;
	}

	// 前置递增运算符重载
	// NOTE:前置递增运算符重载，返回值必须是引用，因为要对同一个对象实现递增
	Person& operator++() {
		this->age++;
		return *this;
	}

	// 后置递增运算符重载
	// NOTE: 
	//		使用整形（必须是整形）参数占位符代表后置递增运算符重载
	//		返回值必须是值返回，否则局部变量被回收会导致内存访问错误
	Person operator++(int) {
		Person personTemp = *this;
		this->age++;
		return personTemp;
	}
};

//// 全局函数加号运算符重载
//Person operator+(Person &person1, Person &person2) {
//	Person personTemp;
//	personTemp.age = person1.age + person2.age;
//	return personTemp;
//}
//Person operator+(Person &person1, int age) {
//	Person personTemp;
//	personTemp.age = person1.age + age;
//	return personTemp;
//}

// NOTE：只能使用全局函数实现左移运算符重载
// 不能使用成员函数实现左移运算符重载
ostream & operator<<(ostream &cout, Person &person) {
	cout << "age=" << person.age;
	return cout;
}

int main() {
	// 加号运算符重载
	Person person1;
	Person person2;
	person1.age = 10;
	person2.age = 11;
	Person person3 = person1 + person2;
	cout << person3 << endl;
	Person person5 = person1 + 12;
	cout << person5 << endl;

	// 递增运算符重载
	Person person6;
	cout << "前置递增运算符重载1，" <<  ++(++person6) << endl;
	cout << "前置递增运算符重载2，" <<  person6 << endl;

	Person person7;
	cout << "后置递增运算符重载1，" <<  person7++ << endl;
	cout << "后置递增运算符重载2，" <<  person7 << endl;

	system("pause");
	return 0;
}