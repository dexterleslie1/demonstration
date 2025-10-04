#ifndef PAGELISTBYUSERID_H
#define PAGELISTBYUSERID_H

#include <QWidget>
#include <QSettings>
#include <QtNetwork/QNetworkAccessManager>

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
    QSettings qSettings;
    QNetworkAccessManager *networkAccessManager;
};

#endif // PAGELISTBYUSERID_H
