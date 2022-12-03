#include "Circle.h"

void Circle::setR(int r) {
	this->r = r;
}

void Circle::setCenter(Point center) {
	this->center = center;
}

int Circle::getR() {
	return this->r;
}

Point Circle::getCenter() {
	return this->center;
}

void Circle::print() {
	cout << "center x=" << this->center.getX() << ",y=" << this->center.getY() << ",r=" << this->r << endl;
}