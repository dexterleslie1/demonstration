#include "widget.h"
#include "ui_widget.h"

#include <QDebug>

Widget::Widget(QWidget *parent)
    : QWidget(parent)
    , ui(new Ui::Widget)
{
    ui->setupUi(this);

    // 初始化 QComboBox
    ui->comboBox->addItem("全部", QVariant(""));
    ui->comboBox->addItem("未支付", QVariant("Unpay"));
    ui->comboBox->addItem("未发货", QVariant("Undelivery"));

    // 点击查询按钮
    connect(ui->pushButton, &QPushButton::clicked, this, [this](){
        // 获取当前用户数据
        QVariant variant = ui->comboBox->currentData();
        QString status = variant.toString();
        qDebug() << "data=" << status;
    });
}

Widget::~Widget()
{
    delete ui;
}

