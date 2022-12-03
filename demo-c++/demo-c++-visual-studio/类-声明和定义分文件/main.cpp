#include <iostream>
#include "Circle.h"

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