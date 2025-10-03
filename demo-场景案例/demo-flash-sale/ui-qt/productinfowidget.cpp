#include "productinfowidget.h"
#include "ui_productinfowidget.h"

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

void ProductInfoWidget::configureWithData(long id,
                                          QString name,
                                          int stockAmount,
                                          bool flashSale,
                                          int toFlashSaleStartTimeRemainingSeconds,
                                          int toFlashSaleEndTimeRemainingSeconds) {
    this->id = id;
    this->name = name;
    this->stockAmount = stockAmount;
    this->flashSale = flashSale;
    this->toFlashSaleStartTimeRemainingSeconds = toFlashSaleStartTimeRemainingSeconds;
    this->toFlashSaleEndTimeRemainingSeconds = toFlashSaleEndTimeRemainingSeconds;

    this->ui->labelName->setText(this->name);
    this->ui->labelStockAmount->setText(QString("%1").arg(this->stockAmount));
    this->ui->labelStatus->setVisible(this->flashSale);

    if(this->flashSale) {
        if(this->toFlashSaleStartTimeRemainingSeconds > 0) {
            this->ui->labelStatus->setText(QString("距离秒杀开始时间还有 %1 秒").arg(this->toFlashSaleStartTimeRemainingSeconds));
            this->ui->labelStatus->setStyleSheet("color: #FFA500");
        } else if(this->toFlashSaleStartTimeRemainingSeconds <= 0 && this->toFlashSaleEndTimeRemainingSeconds > 0) {
            this->ui->labelStatus->setText(QString("距离秒杀结束还有 %1 秒").arg(this->toFlashSaleEndTimeRemainingSeconds));
            this->ui->labelStatus->setStyleSheet("color: #9ACD32");
        } else if(this->toFlashSaleStartTimeRemainingSeconds <= 0 && this->toFlashSaleEndTimeRemainingSeconds <= 0) {
            this->ui->labelStatus->setText("秒杀已结束");
            this->ui->labelStatus->setStyleSheet("color: #FF0000");
        }
    }
}
