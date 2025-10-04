#ifndef PAGELISTBYMERCHANTID_H
#define PAGELISTBYMERCHANTID_H

#include <QWidget>
#include <QSettings>
#include <QtNetwork/QNetworkAccessManager>

namespace Ui {
class PageListByMerchantId;
}

class PageListByMerchantId : public QWidget
{
    Q_OBJECT

public:
    explicit PageListByMerchantId(QWidget *parent = nullptr);
    ~PageListByMerchantId();

private:
    Ui::PageListByMerchantId *ui;
    QSettings qSettings;
    QNetworkAccessManager *networkAccessManager;
};

#endif // PAGELISTBYMERCHANTID_H
