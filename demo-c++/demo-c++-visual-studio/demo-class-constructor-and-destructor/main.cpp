﻿#include <iostream>

using namespace std;

// 演示构造函数和析构函数的用法

// NOTE: 如果没有定义构造函数和析构函数，
// 编译器会生成默认无参构造、默认析构函数、默认拷贝函数（对属性进行拷贝）三个函数
class Person {
private:
	int age;

public:
	Person() {
		cout << "无参构造函数被调用" << endl;
	}

	Person(int age) {
		cout << "有参构造函数被调用" << endl;
		this->age = age;
	}

	Person(const Person &person) {
		cout << "拷贝构造函数被调用" << endl;
		this->age = person.age;
	}

	// 析构函数
	~Person() {
		cout << "析构函数被调用" << endl;
	}
};

// 测试局部变量被回收时会自动调用析构函数
void test() {
	Person person;
}

// 测试构造函数调用方式
// 1、括号法 2、显式法 3、隐式法
void test1() {
	/* 括号法调用构造函数 */
	// 无参构造函数
	//Person person1;
	// 有参构造函数
	//Person person2(10);
	// 拷贝构造函数
	//Person person3(person2);

	/* 显式法调用构造函数 */
	//Person person1;
	//Person person2 = Person(10);
	//Person person3 = Person(person2);

	/* 隐式法调用构造函数 */
	//Person person1 = 10;
	//Person person2 = person1;
}

void function_test1(Person person) {

}

Person function_test2() {
	Person person;
	return person;
}

// 拷贝构造函数使用场景
void test_copy_constructor_invoking_situation() {
	// 1、使用一个已经初始化的对象创建一个新的对象
	// Person person1 = Person(10);
	// Person person2 = Person(person1);

	// 2、以值传递的方式传递参数
	// Person person;
	// function_test1(person);

	// 3、值方式返回局部对象
	// Person person = function_test2();
}

// 测试编译器默认构造函数生成规则
class Person1Test {
private:
	int age;
public:
	Person1Test(int age) {
		this->age = age;
	}

	int getAge() {
		return age;
	}
};

void test_compiler_default_constructor_generate_rule() {
	// 1、如果提供有参构造函数，编译器不生成无参构造函数，但依旧生成默认拷贝构造函数
	// 编译时错误提示“没有合适的默认构造函数可用”
	// Person1Test person1;

	// 依旧生成默认拷贝构造函数
	Person1Test person1(18);
	Person1Test person2(person1);
	cout << "age=" << person2.getAge() << endl;

	// 2、如果提供拷贝构造函数，编译器不生成无参和有参构造函数
	// 编译时错误提示“没有合适的默认构造函数可用”
	// Person1Test person3;
}

// 浅拷贝和深拷贝构造函数
class Person2Test {
private:
	int *height;
public:
	Person2Test(int height) {
		this->height = new int(height);
		cout << "有参构造函数被调用" << endl;
	}

	~Person2Test() {
		if(this->height != NULL) {
			delete this->height;
			this->height = NULL;
			cout << "析构函数被调用" << endl;
		}
	}
};
class Person3Test {
private:
	int *height;
public:
	Person3Test(int height) {
		this->height = new int(height);
		cout << "有参构造函数被调用" << endl;
	}

	Person3Test(Person3Test &person) {
		this->height = new int(*person.height);
		cout << "拷贝函数被调用" << endl;
	}

	~Person3Test() {
		if(this->height != NULL) {
			delete this->height;
			this->height = NULL;
			cout << "析构函数被调用" << endl;
		}
	}
};

void test_shallow_and_deep_clone_constructor() {
	// 编译器默认的拷贝构造函数是浅拷贝
	// 会导致堆内存重复释放问题
	//Person2Test person1 = Person2Test(160);
	//Person2Test person2 = Person2Test(person1);

	// 自定义拷贝构造函数解决浅拷贝导致堆内存重复释放问题
	Person3Test person1 = Person3Test(160);
	Person3Test person2 = Person3Test(person1);
}

// 测试属性初始化列表
class Person5Test{
public:
	int a;
	int b;
	
	Person5Test():a(10), b(20) {
	}

	Person5Test(int a, int b):a(a), b(b) {

	}
};

void test_initialization_list() {
	Person5Test person1;
	cout << "a=" << person1.a << ",b=" << person1.b << endl;

	Person5Test person2(11, 22);
	cout << "a=" << person2.a << ",b=" << person2.b << endl;
}

int main() {
	// test();
	// test1();
	// test_copy_constructor_invoking_situation();
	// test_compiler_default_constructor_generate_rule();
	// test_shallow_and_deep_clone_constructor();
	test_initialization_list();

	system("pause");
	return 0;
}