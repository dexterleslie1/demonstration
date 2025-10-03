#include "widget.h"
#include "ui_widget.h"
#include <QSettings>

Widget::Widget(QWidget *parent)
    : QWidget(parent)
    , ui(new Ui::Widget)
{
    ui->setupUi(this);

    // 创建主窗口
    this->mainWidget = new MainWidget();

    // 读取上次保存的 userId 和 merchantId
    QString userId = this->qSettings.value("userId", "").toString();
    QString merchantId = this->qSettings.value("merchantId", "").toString();
    ui->iDLineEdit->setText(userId);
    ui->iDLineEdit_2->setText(merchantId);

    // 点击确定按钮
    connect(ui->pushButton, &QPushButton::clicked, this, [this](){
        // 获取 userId 和 merchantId
        QString userId = ui->iDLineEdit->text();
        QString merchantId = ui->iDLineEdit_2->text();

        qSettings.setValue("userId", userId);
        qSettings.setValue("merchantId", merchantId);
        qSettings.sync();

        // 跳转到主窗口
        this->hide();
        this->mainWidget->show();
    });

    // 接收 backToInfo 信号
    connect(this->mainWidget, &MainWidget::backToInfo, this, [this](){
        this->mainWidget->hide();
        this->show();
    });
}

Widget::~Widget()
{
    delete this->mainWidget;
    delete ui;
}

