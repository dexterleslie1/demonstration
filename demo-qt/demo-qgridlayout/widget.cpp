#include "widget.h"
#include "ui_widget.h"
#include "productinfowidget.h"
#include <QtDebug>

Widget::Widget(QWidget *parent)
    : QWidget(parent)
    , ui(new Ui::Widget)
{
    ui->setupUi(this);

    ui->gridLayout->addWidget(new ProductInfoWidget(this), 0, 0);
    ui->gridLayout->addWidget(new ProductInfoWidget(this), 0, 1);
    ui->gridLayout->addWidget(new ProductInfoWidget(this));
    ui->gridLayout->addWidget(new ProductInfoWidget(this));
    ui->gridLayout->addWidget(new ProductInfoWidget(this));
}

Widget::~Widget()
{
    delete ui;
}

