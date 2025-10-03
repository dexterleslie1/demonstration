#include "productinfowidget.h"
#include "ui_productinfowidget.h"
#include <QDebug>

ProductInfoWidget::ProductInfoWidget(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::ProductInfoWidget)
{
    ui->setupUi(this);
}

ProductInfoWidget::~ProductInfoWidget()
{
    delete ui;
}
