#ifndef PAGELISTBYUSERID_H
#define PAGELISTBYUSERID_H

#include <QWidget>

namespace Ui {
class PageListByUserId;
}

class PageListByUserId : public QWidget
{
    Q_OBJECT

public:
    explicit PageListByUserId(QWidget *parent = nullptr);
    ~PageListByUserId();

private:
    Ui::PageListByUserId *ui;
};

#endif // PAGELISTBYUSERID_H
