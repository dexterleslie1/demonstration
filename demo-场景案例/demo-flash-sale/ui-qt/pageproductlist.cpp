#include "pageproductlist.h"
#include "ui_pageproductlist.h"
#include <QtNetwork/QNetworkAccessManager>
#include <QtNetwork/QNetworkReply>
#include <QJsonDocument>
#include <QJsonObject>
#include <QJsonArray>
#include <QProgressDialog>
#include <QMessageBox>
#include <QThread>

#include "productinfowidget.h"

PageProductList::PageProductList(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::PageProductList)
{
    ui->setupUi(this);

    this->networkAccessManager = new QNetworkAccessManager(this);

    // 点击刷新商品列表按钮
    connect(ui->pushButtonRefresh, &QPushButton::clicked, this, [this](){
        this->reloadProductList();
    });
}

PageProductList::~PageProductList()
{
    delete this->networkAccessManager;
    delete ui;
}

void PageProductList::reloadProductList() {
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
    QUrl url = QUrl("http://192.168.235.128:8080/api/v1/order/listProductByIds");
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
                QJsonArray data = jsonObject["data"].toArray();
                if(errorCode > 0) {
                    QMessageBox::critical(this, "错误", errorMessage);
                } else {
                    // 删除 gridLayout 所有子元素
                    int count = this->ui->gridLayout->count();
                    for(int i=0;i<count;i++) {
                        QLayoutItem *item = this->ui->gridLayout->itemAt(i);
                        QWidget *widget = item->widget();
                        widget->deleteLater();
                    }

                    for(int i=0;i<data.size();i++) {
                        QJsonObject productObject = data[i].toObject();
                        long id = productObject["id"].toVariant().LongLong;
                        QString name = productObject["name"].toString();
                        int stockAmount = productObject["stock"].toInt();
                        bool flashSale = productObject["flashSale"].toBool();
                        int toFlashSaleStartTimeRemainingSeconds = productObject["toFlashSaleStartTimeRemainingSeconds"].toInt();
                        int toFlashSaleEndTimeRemainingSeconds = productObject["toFlashSaleEndTimeRemainingSeconds"].toInt();
                        ProductInfoWidget *widget = new ProductInfoWidget(this);
                        widget->configureWithData(id, name, stockAmount, flashSale, toFlashSaleStartTimeRemainingSeconds, toFlashSaleEndTimeRemainingSeconds);
                        this->ui->gridLayout->addWidget(widget, (i-i%2)/2, i%2);
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
