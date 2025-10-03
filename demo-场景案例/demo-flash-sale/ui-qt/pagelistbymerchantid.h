#ifndef PAGELISTBYMERCHANTID_H
#define PAGELISTBYMERCHANTID_H

#include <QWidget>

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
};

#endif // PAGELISTBYMERCHANTID_H
