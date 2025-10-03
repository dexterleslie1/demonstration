#include "widget.h"
#include "ui_widget.h"

Widget::Widget(QWidget *parent)
    : QWidget(parent)
    , ui(new Ui::Widget)
{
    ui->setupUi(this);

    // 初始化 QListWidget
    for(int i=0;i<30;i++) {
        ui->listWidget->addItem(QString("%1").arg(i));
    }
}

Widget::~Widget()
{
    delete ui;
}

