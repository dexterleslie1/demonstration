#pragma once

#include "Point.h"
#include <iostream>

using namespace std;

class Circle {
private:
	int r;
	Point center;

public:
	void setR(int r);
	void setCenter(Point center);
	int getR();
	Point getCenter();

	void print();
};