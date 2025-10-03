#include "productpurchasewidget.h"
#include "ui_productpurchasewidget.h"

#include <QtNetwork/QNetworkAccessManager>
#include <QtNetwork/QNetworkReply>
#include <QJsonDocument>
#include <QJsonObject>
#include <QJsonArray>
#include <QProgressDialog>
#include <QMessageBox>
#include <QThread>
#include <QDebug>

ProductPurchaseWidget::ProductPurchaseWidget(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::ProductPurchaseWidget)
{
    ui->setupUi(this);

    this->networkAccessManager = new QNetworkAccessManager(this);

    // 点击购买按钮
    connect(ui->pushButtonPurchase, &QPushButton::clicked, this, [this]() {
        // 显示等待对话框
        QProgressDialog *progress = new QProgressDialog(this);
        progress->setLabelText("处理中...");
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

        QString userIdStr = this->qSettings.value("userId", "").toString();
        QUrl url;
        if(this->flashSale) {
            url = QUrl(QString("http://192.168.235.128:8080/api/v1/order/createFlashSale?userId=%1&productId=%2&randomCreateTime=true").arg(userIdStr).arg(this->productId));
        } else {
            url = QUrl(QString("http://192.168.235.128:8080/api/v1/order/create?userId=%1&productId=%2&randomCreateTime=true").arg(userIdStr).arg(this->productId));
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
                        QMessageBox::information(this, "提示", data);
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

ProductPurchaseWidget::~ProductPurchaseWidget()
{
    delete ui;
}

void ProductPurchaseWidget::configureWithData(long id) {
    this->productId = id;

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
    QUrl url = QUrl(QString("http://192.168.235.128:8080/api/v1/order/getProductById?id=%1").arg(this->productId));
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
                QJsonObject data = jsonObject["data"].toObject();
                if(errorCode > 0) {
                    QMessageBox::critical(this, "错误", errorMessage);
                } else {
                    QString name = data["name"].toString();
                    int stockAmount = data["stock"].toInt();
                    this->flashSale = data["flashSale"].toBool();
                    int toFlashSaleStartTimeRemainingSeconds = data["toFlashSaleStartTimeRemainingSeconds"].toInt();
                    int toFlashSaleEndTimeRemainingSeconds = data["toFlashSaleEndTimeRemainingSeconds"].toInt();
                    this->ui->labelName->setText(name);
                    this->ui->labelStockAmount->setText(QString("%1").arg(stockAmount));
                    this->ui->labelStatus->setVisible(this->flashSale);
                    if(this->flashSale) {
                        if(toFlashSaleStartTimeRemainingSeconds > 0) {
                            this->ui->labelStatus->setText(QString("距离秒杀开始时间还有 %1 秒").arg(toFlashSaleStartTimeRemainingSeconds));
                            this->ui->labelStatus->setStyleSheet("color: #FFA500");
                        } else if(toFlashSaleStartTimeRemainingSeconds <= 0 && toFlashSaleEndTimeRemainingSeconds > 0) {
                            this->ui->labelStatus->setText(QString("距离秒杀结束还有 %1 秒").arg(toFlashSaleEndTimeRemainingSeconds));
                            this->ui->labelStatus->setStyleSheet("color: #9ACD32");
                        } else if(toFlashSaleStartTimeRemainingSeconds <= 0 && toFlashSaleEndTimeRemainingSeconds <= 0) {
                            this->ui->labelStatus->setText("秒杀已结束");
                            this->ui->labelStatus->setStyleSheet("color: #FF0000");
                        }
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
}
