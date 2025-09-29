#include "widget.h"
#include "ui_widget.h"

Widget::Widget(QWidget *parent)
    : QWidget(parent)
    , ui(new Ui::Widget)
{
    ui->setupUi(this);

    // 修改窗口的标题
    this->setWindowTitle("测试QWidget");
    // 设置初始窗口大小
    this->resize(600, 400);

    this->anotherWidget = new AnotherWidget();

    // 点击跳转按钮
    connect(ui->pushButton, &QPushButton::clicked, this, [this](){
        // 隐藏当前窗口
        this->hide();
        // 显示跳转目标窗口
        this->anotherWidget->show();
    });

    // 接收AnotherWidget返回信号
    connect(this->anotherWidget, &AnotherWidget::back, this, [this](){
        // 显示当前窗口
        this->show();
        // 隐藏AnotherWidget窗口
        this->anotherWidget->hide();
    });
}

Widget::~Widget()
{
    delete ui;
}

