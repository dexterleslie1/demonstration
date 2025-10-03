#include "widget.h"
#include "ui_widget.h"
#include "productinfowidget.h"
#include <QtDebug>

Widget::Widget(QWidget *parent)
    : QWidget(parent)
    , ui(new Ui::Widget)
{
    ui->setupUi(this);

    this->reloadProductList();

    // 点击刷新按钮
    connect(ui->pushButtonRefresh, &QPushButton::clicked, this, [this]() {
        // 先删除 gridLayout 的 item
        int count = ui->gridLayout->count();
        for(int i=0;i<count;i++) {
            QLayoutItem *item = ui->gridLayout->itemAt(i);
            QWidget *widget = item->widget();
            widget->deleteLater();
        }

        this->reloadProductList();
    });
}

Widget::~Widget()
{
    delete ui;
}

void Widget::reloadProductList() {
    ui->gridLayout->addWidget(new ProductInfoWidget(this), 0, 0);
    ui->gridLayout->addWidget(new ProductInfoWidget(this), 0, 1);
    ui->gridLayout->addWidget(new ProductInfoWidget(this));
    ui->gridLayout->addWidget(new ProductInfoWidget(this));
    ui->gridLayout->addWidget(new ProductInfoWidget(this));
}

