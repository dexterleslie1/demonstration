#include "student.h"

// Student 构造函数调用基类 Person(name, age) 构造函数
Student::Student(std::string name, int age, int grade)
    :Person(name, age)
{
    this->grade = grade;
    // 调用基类人类打招呼方法
    this->sayHi();
}

Student::~Student() {

}
