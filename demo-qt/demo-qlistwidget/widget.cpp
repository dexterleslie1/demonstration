#include "widget.h"
#include "ui_widget.h"

Widget::Widget(QWidget *parent)
    : QWidget(parent)
    , ui(new Ui::Widget)
{
    ui->setupUi(this);

    // 点击刷新按钮
    connect(ui->pushButton, &QPushButton::clicked, this, [this]() {
        this->reload();
    });
}

Widget::~Widget()
{
    delete ui;
}

void Widget::reload() {
    // 删除所有 item
    ui->listWidget->clear();

    // 初始化 QListWidget
    for(int i=0;i<30;i++) {
        ui->listWidget->addItem(QString("%1").arg(i));
    }
}

