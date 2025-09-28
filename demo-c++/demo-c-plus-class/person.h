#pragma once

// 必须包含 <string> 才能使用 std::string
#include <string>

class Person {
private:
    std::string name;
    int age;

public:
    Person(std::string name, int age);
    ~Person();

    // 人类打招呼
    void sayHi();
};
