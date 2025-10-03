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

private:
    Ui::ProductInfoWidget *ui;
};

#endif // PRODUCTINFOWIDGET_H
