#include "widgetborderwithgroupbox.h"
#include "ui_widgetborderwithgroupbox.h"

WidgetBorderWithGroupBox::WidgetBorderWithGroupBox(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::WidgetBorderWithGroupBox)
{
    ui->setupUi(this);
}

WidgetBorderWithGroupBox::~WidgetBorderWithGroupBox()
{
    delete ui;
}
