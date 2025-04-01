#include <iostream>

using namespace std;

class Person {
public:
	int age;

	Person():age(10) {
	}

	Person& addAge(const Person &person) {
		this->age += person.age;
		// 返回本对象
		return *this;
	}
};

int main() {
	Person person1;
	Person person2;
	person2.addAge(person1).addAge(person1);
	cout << "person2.age=" << person2.age << endl;

	system("pause");
	return 0;
}