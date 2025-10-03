#include "widgetborderwithoutgroupbox.h"
#include "ui_widgetborderwithoutgroupbox.h"

WidgetBorderWithoutGroupBox::WidgetBorderWithoutGroupBox(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::WidgetBorderWithoutGroupBox)
{
    ui->setupUi(this);
}

WidgetBorderWithoutGroupBox::~WidgetBorderWithoutGroupBox()
{
    delete ui;
}
