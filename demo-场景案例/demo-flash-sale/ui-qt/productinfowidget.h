#ifndef PRODUCTINFOWIDGET_H
#define PRODUCTINFOWIDGET_H

#include <QWidget>

namespace Ui {
class ProductInfoWidget;
}

class ProductInfoWidget : public QWidget
{
    Q_OBJECT

public:
    explicit ProductInfoWidget(QWidget *parent);
    ~ProductInfoWidget();
    void configureWithData(long id,
                           QString name,
                           int stockAmount,
                           bool flashSale,
                           int toFlashSaleStartTimeRemainingSeconds,
                           int toFlashSaleEndTimeRemainingSeconds);

private:
    Ui::ProductInfoWidget *ui;
    long id;
    QString name;
    int stockAmount;
    bool flashSale;
    int toFlashSaleStartTimeRemainingSeconds;
    int toFlashSaleEndTimeRemainingSeconds;

signals:
    void clicked(long productId);

protected:
    void mousePressEvent(QMouseEvent *event) override;
};

#endif // PRODUCTINFOWIDGET_H
