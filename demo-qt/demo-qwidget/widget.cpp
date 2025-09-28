#include "widget.h"

Widget::Widget(QWidget *parent)
    : QWidget(parent)
{
    // 修改窗口的标题
    this->setWindowTitle("测试QWidget");
    // 设置初始窗口大小
    this->resize(600, 400);
}

Widget::~Widget()
{
}

