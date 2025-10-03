#include "pagelistbyuserid.h"
#include "ui_pagelistbyuserid.h"

PageListByUserId::PageListByUserId(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::PageListByUserId)
{
    ui->setupUi(this);
}

PageListByUserId::~PageListByUserId()
{
    delete ui;
}
