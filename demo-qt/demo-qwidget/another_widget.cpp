#include "another_widget.h"
#include "ui_another_widget.h"

AnotherWidget::AnotherWidget(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::AnotherWidget)
{
    ui->setupUi(this);

    // 点击返回按钮
     connect(ui->pushButton, &QPushButton::clicked, this, &AnotherWidget::back);
}

AnotherWidget::~AnotherWidget()
{
    delete ui;
}
