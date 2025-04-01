#include <iostream>
#include <string>

using namespace std;

class Building;
// 友元的作用：授权外部能够访问类的私有成员

class GoodGay {
public:
	Building *building;

	GoodGay();

	~GoodGay();

	void visit();
};

class GoodGay2 {
public:
	Building *building;

	GoodGay2();
	~GoodGay2();

	void visit();

	void visit2();
};

class Building {
	// 声明全局函数是友元函数
	friend void test_friend_global_function();
	friend class GoodGay;
	friend void GoodGay2::visit();

public:
	Building();

public:
	string sittingroom;

private:
	string bedroom;
};

Building::Building() {
	this->sittingroom = "客厅";
	this->bedroom = "卧室";
}

GoodGay::GoodGay() {
	this->building = new Building;
}
GoodGay::~GoodGay() {
	if(this->building != NULL) {
		delete this->building;
		this->building = NULL;
	}
}
void GoodGay::visit() {
	cout << "友元类GoodGay正在访问：" << this->building->sittingroom << endl;
	cout << "友元类GoodGay正在访问：" << this->building->bedroom << endl;
}

GoodGay2::GoodGay2() {
	this->building = new Building;
}
GoodGay2::~GoodGay2() {
	if(this->building != NULL) {
		delete this->building;
		this->building = NULL;
	}
}
void GoodGay2::visit() {
	cout << "友元成员函数GoodGay2::visit正在访问：" << this->building->sittingroom << endl;
	cout << "友元成员函数GoodGay2::visit正在访问：" << this->building->bedroom << endl;
}
void GoodGay2::visit2() {
	cout << "友元成员函数GoodGay2::visit2正在访问：" << this->building->sittingroom << endl;
	// visit2不是友元成员函数，所以不能访问bedroom私有成员
	//cout << "友元成员函数GoodGay2::visit2正在访问：" << this->building->bedroom << endl;
}

// 友元全局函数
void test_friend_global_function() {
	Building building;
	cout << "全局函数正在访问：" << building.sittingroom << endl;
	// 能够访问Building的私有成员变量bedroom
	cout << "全局函数正在访问：" << building.bedroom << endl;
}

// 友元类
void test_friend_class() {
	GoodGay goodgay;
	goodgay.visit();
}

// 友元成员函数
void test_friend_class_member_method() {
	GoodGay2 goodgay;
	goodgay.visit();
	goodgay.visit2();
}

int main() {
	// test_friend_global_function();
	//test_friend_class();
	test_friend_class_member_method();

	system("pause");
	return 0;
}