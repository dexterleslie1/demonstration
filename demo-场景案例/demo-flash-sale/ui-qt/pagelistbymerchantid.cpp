#include "pagelistbymerchantid.h"
#include "ui_pagelistbymerchantid.h"

PageListByMerchantId::PageListByMerchantId(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::PageListByMerchantId)
{
    ui->setupUi(this);
}

PageListByMerchantId::~PageListByMerchantId()
{
    delete ui;
}
