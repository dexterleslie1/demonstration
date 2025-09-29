#include "widget.h"
#include <QPushButton>
#include <QtDebug>

Widget::Widget(QWidget *parent)
    : QWidget(parent)
{
    this->resize(600, 400);

    // 添加关闭窗口按钮
    QPushButton *button = new QPushButton("关闭窗口", this);
    // 连接信号和槽
    // sender 信号的发送者
    // 按钮的 clicked()信号，当用户点击按钮时触发。
    // ​​槽函数的接收者​​（通常是当前类的实例，这里是窗口对象）。
    // 窗口的 close()槽函数，调用时会关闭窗口。
    connect(button, &QPushButton::clicked, this, &QWidget::close);

    // 槽使用lambda表达式
    connect(button, &QPushButton::clicked, this, [](){
        qDebug() << "点击按钮";
    });
}

Widget::~Widget()
{
}

