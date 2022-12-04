#include <iostream>
#include "Circle.h"

// https://blog.csdn.net/weixin_45407700/article/details/114269876
int main() {
	Point point;
	point.setX(100);
	point.setY(101);

	Circle circle;
	circle.setR(200);
	circle.setCenter(point);
	circle.print();

	system("pause");
	return 0;
}