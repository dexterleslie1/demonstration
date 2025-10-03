#ifndef STUDENT_H
#define STUDENT_H

#include "person.h"

// Student 继承 Person 类
class Student : public Person
{
private:
    int grade;
public:
    Student(std::string name, int age, int grade);
    ~Student();

    // 声明 sayHi 方法准备重写
    void sayHi();
};

#endif // STUDENT_H
