#include <iostream>
#include "Circle.h"
#include "student.h"

// https://blog.csdn.net/weixin_45407700/article/details/114269876
int main() {
    Point point;
    point.setX(100);
    point.setY(101);

    Circle circle;
    circle.setR(200);
    circle.setCenter(point);
    circle.print();

    // 测试类继承
    std::string name = "Dexter";
    int age = 11;
    int grade = 30;
    Student student = Student(name, age, grade);

    return 0;
}
