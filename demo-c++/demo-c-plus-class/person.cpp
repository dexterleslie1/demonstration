#include "person.h"
#include <iostream>

Person::Person(std::string name, int age) {
    this->name = name;
    this->age = age;
}

Person::~Person() {

}

void Person::sayHi() {
    std::cout << "Person say hi to " << this->name << "(age=" << this->age << ")" << std::endl;
}
