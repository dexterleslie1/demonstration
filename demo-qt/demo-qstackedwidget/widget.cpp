#include "widget.h"
#include "ui_widget.h"

Widget::Widget(QWidget *parent)
    : QWidget(parent)
    , ui(new Ui::Widget)
{
    ui->setupUi(this);

    // 初始化 StackedWidget 的 Page
    this->page1 = new Page1(ui->stackedWidget);
    this->page2 = new Page2(ui->stackedWidget);
    this->page3 = new Page3(ui->stackedWidget);
    ui->stackedWidget->addWidget(page1);
    ui->stackedWidget->addWidget(page2);
    ui->stackedWidget->addWidget(page3);
    // 设置默认显示 Page
    ui->stackedWidget->setCurrentWidget(this->page1);

    // 点击 Page 按钮
    connect(ui->pushButton, &QPushButton::clicked, this, [this](){
        // 切换到 page1
       ui->stackedWidget->setCurrentWidget(this->page1);
    });
    connect(ui->pushButton_2, &QPushButton::clicked, this, [this](){
       ui->stackedWidget->setCurrentWidget(this->page2);
    });
    connect(ui->pushButton_3, &QPushButton::clicked, this, [this](){
       ui->stackedWidget->setCurrentWidget(this->page3);
    });
}

Widget::~Widget()
{
    delete ui;
}

