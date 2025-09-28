// 当多个源文件（`.cpp`）包含同一个头文件（`.h`）时，`#pragma once` 确保该头文件在**单个编译单元中仅被展开一次**，避免因重复定义导致的编译错误。
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
