#include <QtTest>
#include <QCoreApplication>
#include <QtNetwork/QNetworkAccessManager>
#include <QtNetwork/QNetworkReply>

#include "qtexpectation.h"

// add necessary includes here

class TestMyTest : public QObject
{
    Q_OBJECT

public:
    TestMyTest();
    ~TestMyTest();

private slots:
    void test_case1();

};

TestMyTest::TestMyTest()
{

}

TestMyTest::~TestMyTest()
{

}

void TestMyTest::test_case1()
{
    QString host = "192.168.235.128";
    int port = 8080;

    // 所有请求共享 QNetworkAccessManager 实例
    QNetworkAccessManager *manager = new QNetworkAccessManager(this);

    // 测试 HTTP 200 响应
    QtExpectation *exp = new QtExpectation(2000);
    QUrl url = QUrl(QString("http://%1:%2/api/v1/xxx").arg(host).arg(port));
    QNetworkRequest request = QNetworkRequest(url);
    request.setHeader(QNetworkRequest::ContentTypeHeader, "application/json; charset=utf-8");
    QNetworkReply *reply = manager->get(request);
    int errorCode = 0;
    QString errorMessage;
    QJsonValue data;
    QString nonHttp20xResponse;
    connect(reply, &QNetworkReply::finished, this, [reply, exp, &errorCode, &errorMessage, &data, &nonHttp20xResponse]() {
        if (reply->error() == QNetworkReply::NoError) {
            // 解析 json 为 QJsonDocument
            QJsonParseError jsonParseError;
            QJsonDocument jsonDocument = QJsonDocument::fromJson(reply->readAll(), &jsonParseError);
            QJsonObject jsonObject = jsonDocument.object();
            errorCode = jsonObject["errorCode"].toInt();
            errorMessage = jsonObject["errorMessage"].toString();
            data = jsonObject["data"];
        } else {
            // 输出错误信息（包括 HTTP 状态码）
            QVariant statusCode = reply->attribute(QNetworkRequest::HttpStatusCodeAttribute);
            QString errorStr = reply->errorString();
            QString responseStr = QString::fromUtf8(reply->readAll());
            QString url = reply->url().toString();
            nonHttp20xResponse = QString("HTTP 请求错误，状态码：%1，原因：%2，服务器响应：%3，url：%4")
                                         .arg(statusCode.toInt())
                                         .arg(errorStr)
                                         .arg(responseStr)
                                         .arg(url);
        }
        // 手动释放 reply
        reply->deleteLater();

        exp->fulfill();
    });

    QVERIFY(exp->wait());

    QVERIFY2(errorCode == 90000, QString("errorCode=%1").arg(errorCode).toUtf8());
    QVERIFY2(errorMessage == "资源 /api/v1/xxx 不存在！", QString("errorMessage=%1").arg(errorMessage).toUtf8());
    QVERIFY(data.isNull());

    exp = new QtExpectation(2000);
    url = QUrl(QString("http://%1:%2/api/v1/testHttp400").arg(host).arg(port));
    request = QNetworkRequest(url);
    request.setHeader(QNetworkRequest::ContentTypeHeader, "application/json; charset=utf-8");
    reply = manager->get(request);
    errorCode = 0;
    errorMessage = QString();
    data = QJsonValue();
    nonHttp20xResponse = QString();
    connect(reply, &QNetworkReply::finished, this, [reply, exp, &errorCode, &errorMessage, &data, &nonHttp20xResponse]() {
        if (reply->error() == QNetworkReply::NoError) {
            // 解析 json 为 QJsonDocument
            QJsonParseError jsonParseError;
            QJsonDocument jsonDocument = QJsonDocument::fromJson(reply->readAll(), &jsonParseError);
            QJsonObject jsonObject = jsonDocument.object();
            errorCode = jsonObject["errorCode"].toInt();
            errorMessage = jsonObject["errorMessage"].toString();
            data = jsonObject["data"];
        } else {
            // 输出错误信息（包括 HTTP 状态码）
            QVariant statusCode = reply->attribute(QNetworkRequest::HttpStatusCodeAttribute);
            QString errorStr = reply->errorString();
            QString responseStr = QString::fromUtf8(reply->readAll());
            QString url = reply->url().toString();
            nonHttp20xResponse = QString("HTTP 请求错误，状态码：%1，原因：%2，服务器响应：%3，url：%4")
                                         .arg(statusCode.toInt())
                                         .arg(errorStr)
                                         .arg(responseStr)
                                         .arg(url);
        }
        // 手动释放 reply
        reply->deleteLater();

        exp->fulfill();
    });

    QVERIFY(exp->wait());

    QVERIFY2(errorCode == 0, QString("errorCode=%1").arg(errorCode).toUtf8());
    QVERIFY2(errorMessage == "", QString("errorMessage=%1").arg(errorMessage).toUtf8());
    QVERIFY(data.isNull());
    QVERIFY2(nonHttp20xResponse == "HTTP 请求错误，状态码：400，原因：Error transferring http://192.168.235.128:8080/api/v1/testHttp400 - server replied: ，服务器响应：{\"errorCode\":90000,\"errorMessage\":\"测试业务异常\",\"data\":null}，url：http://192.168.235.128:8080/api/v1/testHttp400", nonHttp20xResponse.toUtf8());

    delete exp;
    manager->deleteLater();
    delete manager;
}

// 使用 QTEST_GUILESS_MAIN 替代 QTEST_APPLESS_MAIN，否则 QtExpectation 使用时报告错误
QTEST_GUILESS_MAIN(TestMyTest)

#include "tst_testmytest.moc"
