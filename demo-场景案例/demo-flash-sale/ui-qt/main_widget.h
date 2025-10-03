#ifndef MAIN_WIDGET_H
#define MAIN_WIDGET_H

#include <QWidget>
#include <QSettings>
#include "pageproductlist.h"
#include "pagelistbyuserid.h"
#include "pagelistbymerchantid.h"
#include "pagecreateproduct.h"

namespace Ui {
class MainWidget;
}

class MainWidget : public QWidget
{
    Q_OBJECT

public:
    explicit MainWidget(QWidget *parent = nullptr);
    ~MainWidget();

private:
    Ui::MainWidget *ui;
    QSettings qSettings;
    PageProductList *pageProductList;
    PageListByUserId *pageListByUserId;
    PageListByMerchantId *pageListByMerchantId;
    PageCreateProduct *pageCreateProduct;
signals:
    // 返回 userId 和 merchantId 填写页面信号
    void backToInfo();

protected:
    // 重写从 QWidget 继承的 showEvent 方法
    void showEvent(QShowEvent *event) override;
};

#endif // MAIN_WIDGET_H
