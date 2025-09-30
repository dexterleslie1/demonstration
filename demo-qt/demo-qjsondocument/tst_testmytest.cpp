#include <QtTest>
#include <QJsonDocument>
#include <QDebug>

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
    // QJsonObject 转换为 JSON 字符串
    QJsonObject jsonObject;
    jsonObject["name"] = "Dexter";
    jsonObject["age"] = 11;
    // 转换为 JSON 字符串
    QJsonDocument jsonDocument = QJsonDocument(jsonObject);
    QString json = jsonDocument.toJson();
    QVERIFY2(json.toUtf8() == "{\n    \"age\": 11,\n    \"name\": \"Dexter\"\n}\n", json.toUtf8());


    // JSON 字符串创建 QJsonDocument
    jsonDocument = QJsonDocument::fromJson(json.toUtf8());
    jsonObject = jsonDocument.object();
    QVERIFY2("Dexter" == jsonObject["name"].toString().toUtf8(), jsonObject["name"].toString().toUtf8());
    QVERIFY2(11 == jsonObject["age"].toInt(), QString("%1").arg(jsonObject["age"].toInt()).toUtf8());
}

QTEST_APPLESS_MAIN(TestMyTest)

#include "tst_testmytest.moc"
