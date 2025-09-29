#ifndef WIDGET_H
#define WIDGET_H

#include <QWidget>
#include "page1.h"
#include "page2.h"
#include "page3.h"

QT_BEGIN_NAMESPACE
namespace Ui { class Widget; }
QT_END_NAMESPACE

class Widget : public QWidget
{
    Q_OBJECT

public:
    Widget(QWidget *parent = nullptr);
    ~Widget();

private:
    Ui::Widget *ui;
    Page1 *page1;
    Page2 *page2;
    Page3 *page3;
};
#endif // WIDGET_H
