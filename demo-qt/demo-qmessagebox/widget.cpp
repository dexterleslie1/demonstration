#include "widget.h"
#include "ui_widget.h"
#include <QMessageBox>
#include <QDebug>

Widget::Widget(QWidget *parent)
    : QWidget(parent)
    , ui(new Ui::Widget)
{
    ui->setupUi(this);

    // 点击信息提示按钮
    connect(ui->pushButtonInformation, &QPushButton::clicked, this, [this](){
        QMessageBox::information(this, "操作成功", "文件已保存！");
    });
    // 点击错误提示按钮
    connect(ui->pushButtonCritical, &QPushButton::clicked, this, [this](){
       QMessageBox::critical(this,"错误","无法打开文件！");
    });
    // 点击确认对话框按钮
    connect(ui->pushButtonQuestion, &QPushButton::clicked, this, [this](){
        QMessageBox::StandardButton reply =
                QMessageBox::question(this, "确认删除", "确定要删除此文件吗？", QMessageBox::Yes | QMessageBox::No);
        if (reply == QMessageBox::Yes) {
            qDebug() << "用户点击了“是”";
        } else {
            qDebug() << "用户点击了“否”";
        }
    });
}

Widget::~Widget()
{
    delete ui;
}

