#include <iostream>
#include <string>

using namespace std;

struct student {
	string name;
	int age;
};

struct teacher {
	string name;
	student stu;
};

int main() {
	/* 结构体定义和使用 */
	student stu = {"张三", 18};
	cout << "名字：" << stu.name << ",年龄：" << stu.age <<endl;

	/* 结构体数组 */
	student stuArr[] = {
		{"张一", 19},
		{"张二", 20}
	};
	for(int i = 0; i < 2; i++) {
		cout << "[" << i << "]" <<"名字：" << stuArr[i].name << ",年龄：" << stuArr[i].age <<endl;
	}

	/* 结构体指针 */
	student stu1 = {"张五", 21};
	student *pStu = &stu1;
	cout << "名字：" << pStu->name << ",年龄：" << pStu->age <<endl;

	/* 结构体嵌套结构体 */
	teacher teach;
	teach.name = "老师1";
	teach.stu.name = "张六";
	teach.stu.age = 22;
	cout << "老师名字：" << teach.name << "，辅导学生名字：" << teach.stu.name << ",辅导学生年龄：" << teach.stu.age <<endl;

	system("pause");

	return 0;
}