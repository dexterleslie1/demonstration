#include "widget.h"
#include "ui_widget.h"

#include "widgetborderwithgroupbox.h"
#include "widgetborderwithoutgroupbox.h"

Widget::Widget(QWidget *parent)
    : QWidget(parent)
    , ui(new Ui::Widget)
{
    ui->setupUi(this);

    WidgetBorderWithGroupBox *withGroupBox = new WidgetBorderWithGroupBox(this);
    this->ui->verticalLayout->addWidget(withGroupBox);
    WidgetBorderWithoutGroupBox *withoutGroupBox = new WidgetBorderWithoutGroupBox(this);
    this->ui->verticalLayout->addWidget(withoutGroupBox);
}

Widget::~Widget()
{
    delete ui;
}

