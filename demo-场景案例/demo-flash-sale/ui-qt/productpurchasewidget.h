#ifndef PRODUCTPURCHASEWIDGET_H
#define PRODUCTPURCHASEWIDGET_H

#include <QWidget>
#include <QtNetwork/QNetworkAccessManager>
#include <QSettings>

namespace Ui {
class ProductPurchaseWidget;
}

class ProductPurchaseWidget : public QWidget
{
    Q_OBJECT

public:
    explicit ProductPurchaseWidget(QWidget *parent = nullptr);
    ~ProductPurchaseWidget();
    void configureWithData(long id);

private:
    Ui::ProductPurchaseWidget *ui;
    QNetworkAccessManager *networkAccessManager;
    long productId;
    bool flashSale;
    QSettings qSettings;
};

#endif // PRODUCTPURCHASEWIDGET_H
