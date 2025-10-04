#include "pagelistbymerchantid.h"
#include "ui_pagelistbymerchantid.h"

#include <QtNetwork/QNetworkAccessManager>
#include <QtNetwork/QNetworkReply>
#include <QJsonDocument>
#include <QJsonObject>
#include <QJsonArray>
#include <QProgressDialog>
#include <QMessageBox>
#include <QThread>

PageListByMerchantId::PageListByMerchantId(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::PageListByMerchantId)
{
    ui->setupUi(this);

    this->networkAccessManager = new QNetworkAccessManager(this);

    // 初始化状态 QComboBox
    ui->comboBox->addItem("全部", QVariant(""));
    ui->comboBox->addItem("未支付", QVariant("Unpay"));
    ui->comboBox->addItem("未发货", QVariant("Undelivery"));
    ui->comboBox->addItem("未收货", QVariant("Unreceive"));
    ui->comboBox->addItem("已签收", QVariant("Received"));
    ui->comboBox->addItem("买家取消", QVariant("Canceled"));

    // 点击查询按钮
    connect(ui->pushButtonQuery, &QPushButton::clicked, this, [this](){
        QString status = ui->comboBox->currentData().toString();
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

        // 加载订单列表
        QString merchantId = this->qSettings.value("merchantId", "").toString();
        QUrl url;
        if(status.length() == 0) {
            url = QUrl(QString("http://192.168.235.128:8080/api/v1/order/listByMerchantIdAndWithoutStatus?merchantId=%1&latestMonth=true").arg(merchantId));
        } else {
            url = QUrl(QString("http://192.168.235.128:8080/api/v1/order/listByMerchantIdAndStatus?merchantId=%1&latestMonth=true&status=%2").arg(merchantId).arg(status));
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
                    QJsonArray data = jsonObject["data"].toArray();
                    if(errorCode > 0) {
                        QMessageBox::critical(this, "错误", errorMessage);
                    } else {
                        // 删除所有 item
                        ui->listWidget->clear();

                         for(int i=0;i<data.size();i++) {
                             QJsonObject jsonObject = data[i].toObject();
                             long id = jsonObject["id"].toVariant().toLongLong();
                             QString createTime = jsonObject["createTime"].toString();
                             long userId = jsonObject["userId"].toVariant().toLongLong();
                             QJsonArray orderDetailList = jsonObject["orderDetailList"].toArray();
                             QString productList = "";
                             for(int j=0;j<orderDetailList.count();j++) {
                                QJsonObject orderDetailObject = orderDetailList[j].toObject();
                                QString productName = orderDetailObject["productName"].toString();
                                productList = QString("%1，%2").arg(productName).arg(productList);
                             }

                             QString orderStr = QString("订单ID：%1 ，创建时间：%2 ，用户ID：%3 ， 商品列表： %4").arg(id).arg(createTime).arg(userId).arg(productList);
                             ui->listWidget->addItem(orderStr);
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

PageListByMerchantId::~PageListByMerchantId()
{
    delete ui;
}
