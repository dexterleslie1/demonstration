#ifndef PAGEPRODUCTLIST_H
#define PAGEPRODUCTLIST_H

#include <QWidget>
#include <QtNetwork/QNetworkAccessManager>

namespace Ui {
class PageProductList;
}

class PageProductList : public QWidget
{
    Q_OBJECT

public:
    explicit PageProductList(QWidget *parent = nullptr);
    ~PageProductList();
    void reloadProductList();

private:
    Ui::PageProductList *ui;
    QNetworkAccessManager *networkAccessManager;
};

#endif // PAGEPRODUCTLIST_H
