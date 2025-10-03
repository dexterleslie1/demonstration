#include "pagecreateproduct.h"
#include "ui_pagecreateproduct.h"

#include <QtNetwork/QNetworkAccessManager>
#include <QtNetwork/QNetworkReply>
#include <QJsonDocument>
#include <QJsonObject>
#include <QJsonArray>
#include <QProgressDialog>
#include <QMessageBox>
#include <QThread>
#include <QDebug>

PageCreateProduct::PageCreateProduct(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::PageCreateProduct)
{
    ui->setupUi(this);

    this->networkAccessManager = new QNetworkAccessManager(this);
    this->productPurchaseWidget = new ProductPurchaseWidget();

    // 默认隐藏秒杀开始和结束时间
    ui->LabelStartTime->hide();
    ui->LineEditStartTime->hide();
    ui->LabelEndTime->hide();
    ui->LineEditEndTime->hide();

    // 是否秒杀 CheckBox 事件
    connect(ui->CheckBoxFlashSale, &QCheckBox::stateChanged, this, [this](int checked){
        ui->LabelStartTime->setVisible(checked);
        ui->LineEditStartTime->setVisible(checked);
        ui->LabelEndTime->setVisible(checked);
        ui->LineEditEndTime->setVisible(checked);
    });

    connect(ui->pushButtonAdd, &QPushButton::clicked, this, [this]() {
        // 显示等待对话框
        QProgressDialog *progress = new QProgressDialog(this);
        progress->setLabelText("加载中...");
        progress->setCancelButton(nullptr);
        // 禁用窗口关闭按钮
        progress->setWindowFlags(
                    // 保留对话框特性
                    Qt::Dialog |
                    // 禁用默认窗口按钮
                    Qt::CustomizeWindowHint);
        progress->setWindowModality(Qt::WindowModal);
        // 不确定进度模式
        progress->setRange(0, 0);
        // 立即显示
        progress->setMinimumDuration(0);
        progress->show();

        // 加载商品列表
        QUrl url;
        if(!this->ui->CheckBoxFlashSale->isChecked()) {
            url = QUrl("http://192.168.235.128:8080/api/v1/order/addOrdinaryProduct");
        } else {
            url = QUrl("http://192.168.235.128:8080/api/v1/order/addFlashSaleProduct");
        }
        QNetworkRequest request = QNetworkRequest(url);
        request.setHeader(QNetworkRequest::ContentTypeHeader, "application/json; charset=utf-8");
        QNetworkReply *reply = this->networkAccessManager->get(request);
        connect(reply, &QNetworkReply::finished, this, [this, reply, progress]() {
            if (reply->error() == QNetworkReply::NoError) {
                // 解析 json 为 QJsonDocument
                QJsonParseError jsonParseError;
                QByteArray byteArray = reply->readAll();
                QJsonDocument jsonDocument = QJsonDocument::fromJson(byteArray, &jsonParseError);
                if(jsonParseError.error == QJsonParseError::NoError) {
                    QJsonObject jsonObject = jsonDocument.object();
                    int errorCode = jsonObject["errorCode"].toInt();
                    QString errorMessage = jsonObject["errorMessage"].toString();
                    QString data = jsonObject["data"].toString();
                    if(errorCode > 0) {
                        QMessageBox::critical(this, "错误", errorMessage);
                    } else {
                        QMessageBox::StandardButton reply =
                                QMessageBox::question(this, "提示", "成功新增普通商品，是否跳转到商品详情功能并下单呢？", QMessageBox::Yes | QMessageBox::No);
                        if (reply == QMessageBox::Yes) {
                            this->productPurchaseWidget->setWindowModality(Qt::ApplicationModal);
                            this->productPurchaseWidget->configureWithData(data.toLong());
                            this->productPurchaseWidget->show();
                        }
                    }
                } else {
                    QString errorMessage = QString("解析服务器响应 json 时错误，原因：%1，响应：%2")
                            .arg(jsonParseError.errorString())
                            .arg(QString::fromUtf8(byteArray));
                    QMessageBox::critical(this, "错误", errorMessage);
                }
            } else {
                // 输出错误信息（包括 HTTP 状态码）
                QVariant statusCode = reply->attribute(QNetworkRequest::HttpStatusCodeAttribute);
                QString errorStr = reply->errorString();
                QString responseStr = QString::fromUtf8(reply->readAll());
                QString url = reply->url().toString();
                QString errorMessage = QString("HTTP 请求错误，状态码：%1，原因：%2，服务器响应：%3，url：%4")
                                             .arg(statusCode.toInt())
                                             .arg(errorStr)
                                             .arg(responseStr)
                                             .arg(url);
                QMessageBox::critical(this, "错误", errorMessage);
            }
            // 手动释放 reply
            reply->deleteLater();

            progress->close();
            // 安全删除
            progress->deleteLater();
        });
    });
}

PageCreateProduct::~PageCreateProduct()
{
    delete this->productPurchaseWidget;
    delete ui;
}
