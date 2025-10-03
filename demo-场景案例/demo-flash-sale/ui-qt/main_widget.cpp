#include "main_widget.h"
#include "ui_main_widget.h"
#include <QShowEvent>
#include <QDebug>

MainWidget::MainWidget(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::MainWidget)
{
    ui->setupUi(this);

    // 显示 userId 和 merchantId
    QString userId = this->qSettings.value("userId", "").toString();
    QString merchantId = this->qSettings.value("merchantId", "").toString();
    ui->label_2->setText(userId);
    ui->label_4->setText(merchantId);

    // 初始化 Page 页面
    this->pageProductList = new PageProductList(ui->stackedWidget);
    this->pageListByUserId = new PageListByUserId(ui->stackedWidget);
    this->pageListByMerchantId = new PageListByMerchantId(ui->stackedWidget);
    this->pageCreateProduct = new PageCreateProduct(ui->stackedWidget);
    ui->stackedWidget->addWidget(this->pageProductList);
    ui->stackedWidget->addWidget(this->pageListByUserId);
    ui->stackedWidget->addWidget(this->pageListByMerchantId);
    ui->stackedWidget->addWidget(this->pageCreateProduct);
    ui->stackedWidget->setCurrentWidget(this->pageProductList);

    // 点击首页按钮发出 backToInfo 信号
    connect(ui->pushButtonInfo, &QPushButton::clicked, this, &MainWidget::backToInfo);
    // 点击商品列表按钮
    connect(ui->pushButtonProductList, &QPushButton::clicked, this, [this](){
        ui->stackedWidget->setCurrentWidget(this->pageProductList);
    });
    // 点击用户订单按钮
    connect(ui->pushButtonListByUserId, &QPushButton::clicked, this, [this](){
        ui->stackedWidget->setCurrentWidget(this->pageListByUserId);
    });
    // 点击商家订单按钮
    connect(ui->pushButtonListByMerchantId, &QPushButton::clicked, this, [this](){
        ui->stackedWidget->setCurrentWidget(this->pageListByMerchantId);
    });
    // 点击创建商品按钮
    connect(ui->pushButtonCreateProduct, &QPushButton::clicked, this, [this](){
        ui->stackedWidget->setCurrentWidget(this->pageCreateProduct);
    });
}

MainWidget::~MainWidget()
{
    delete ui;
}

void MainWidget::showEvent(QShowEvent *event) {
    QWidget::showEvent(event);

    // 第一次显示主界面时加载商品列表
    this->pageProductList->reloadProductList();
}
