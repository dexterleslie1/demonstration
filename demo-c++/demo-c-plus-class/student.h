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
};

#endif // STUDENT_H
