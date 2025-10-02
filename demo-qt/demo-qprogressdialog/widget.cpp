#include "widget.h"
#include "ui_widget.h"
#include <QProgressDialog>
#include <QTimer>

Widget::Widget(QWidget *parent)
    : QWidget(parent)
    , ui(new Ui::Widget)
{
    ui->setupUi(this);

    connect(ui->pushButton, &QPushButton::clicked, this, [this]() {
        // 显示等待对话框
        QProgressDialog *progress = new QProgressDialog(this);
        progress->setLabelText("正在连接服务器...");
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

        // 异步操作完成时关闭
        QTimer::singleShot(3000, progress, [progress](){
            progress->close();
            // 安全删除
            progress->deleteLater();
        });
    });
}

Widget::~Widget()
{
    delete ui;
}

