#ifndef PAGECREATEPRODUCT_H
#define PAGECREATEPRODUCT_H

#include <QWidget>
#include <QtNetwork/QNetworkAccessManager>

#include "productpurchasewidget.h"

namespace Ui {
class PageCreateProduct;
}

class PageCreateProduct : public QWidget
{
    Q_OBJECT

public:
    explicit PageCreateProduct(QWidget *parent = nullptr);
    ~PageCreateProduct();

private:
    Ui::PageCreateProduct *ui;
    QNetworkAccessManager *networkAccessManager;
    ProductPurchaseWidget *productPurchaseWidget;
};

#endif // PAGECREATEPRODUCT_H
