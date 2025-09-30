# 添加 network 网络模块，否则 QNetworkAccessManager 编译时错误
# 添加 core 模块，否则 QtExpectation 编译时错误
QT += core testlib network
QT -= gui

CONFIG += qt console warn_on depend_includepath testcase
CONFIG -= app_bundle
# 添加 c++11，否则 QtExpectation 编译时错误
CONFIG += c++11

TEMPLATE = app

SOURCES +=  tst_testmytest.cpp

HEADERS += \
    qtexpectation.h
