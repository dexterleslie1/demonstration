#include <QtTest>

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
    int expected = 1;
    QVERIFY2(1 == expected, "非预期测试");
}

QTEST_APPLESS_MAIN(TestMyTest)

#include "tst_testmytest.moc"
