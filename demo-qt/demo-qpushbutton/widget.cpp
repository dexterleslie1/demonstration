#include "widget.h"
#include <QPushButton>

Widget::Widget(QWidget *parent)
    : QWidget(parent)
{
    // 创建 QPushButton 方法一
    // 新增 QPushButton
    QPushButton *button = new QPushButton;
    // 设置按钮的父对象为窗口
    button->setParent(this);
    // 设置按钮显示的文本
    button->setText("第一个按钮");
    // 设置按钮的位置
    button->move(100, 100);

    // 创建 QPushButton 方法二
    new QPushButton("第二个按钮", this);
}

Widget::~Widget()
{
}

