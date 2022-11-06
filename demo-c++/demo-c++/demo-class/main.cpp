#include <iostream>
#include <string>

using namespace std;

// 三种访问权限：private类内可以访问，类外不可以访问，子类不可以访问
// protected类内可以访问，类外不可以访问，子类可以访问
// public类内可以访问，类外可以访问，子类可以访问

// 构造函数分类方式1：无参数构造函数，有参数构造函数
// 构造函数分类方式2：普通构造函数，拷贝构造函数
// 构造函数三种调用方式：括号法、显式发、隐式转换法

//拷贝构造函数调用时机
// 使用已存在的对象创建新的对象 Student(studentInstance)
// 赋值给变量时 Student student = studentInstance
// 作为函数参数以值传递方式传参数 test(Student student)
// 以值方式返回对象 Student test() { Student student; return student;}

// 静态成员变量和函数
// 静态成员变量所有对象共用一个静态成员变量，静态成员变量在链接阶段就已经在全局内存区分配内存
// 静态成员变量在类内定义，类外初始化
class Student {
private:
	string name;
	int age;

	static int m_static;

public:
	// 普通无参数构造函数
	Student() {
		this->name = "";
		this->age = 0;
		cout << "调用Student无参数构造函数" << endl;
	}

	// 普通有参数构造函数
	Student(string name, int age) {
		this->name = name;
		this->age = age;
		cout << "调用Student构造函数Student(string name, int age)" << endl;
	}

	// 拷贝构造函数
	Student(const Student &student) {
		this->name = student.name;
		this->age = student.age;
		cout << "调用Student拷贝构造函数Student(const Student &student)" << endl;
	}

	// 析构函数
	~Student() {
		cout << "调用Student析构函数，名称：" << this->name << ",年龄：" << this->age << endl;
	}

	void display() {
		cout << "名称：" << this->name << ",年龄：" << this->age << endl;
	}

	// 静态成员函数用于访问私有静态成员变量
	static int getMStatic() {
		return m_static;
	}
};

// 初始化静态成员变量
int Student::m_static = 100;

void test_destructor() {
	// 调用默认构造函数，在函数返回时自动调用析构函数
	Student stu;
}

int main() {
	/* 析构函数使用 */
	test_destructor();

	/* 构造函数调用方式 */
	// 括号法
	// 默认构造函数
	Student student1;
	// 调用有参数构造函数
	Student student2("同学1", 10);
	// 拷贝构造函数
	Student student3(student2);
	student3.display();
	
	// 显式法
	// 有参数构造函数
	Student student5 = Student("同学2", 11);
	student5.display();
	// 拷贝构造函数
	Student student6 = Student(student5);
	student6.display();

	// 隐式转换法
	// 拷贝构造函数
	Student student7 = student6;

	/* 静态成员变量和函数 */
	Student student8;
	cout << "Student静态成员变量m_static：" << Student::getMStatic() << endl; 

	system("pause");

	return 0;
}