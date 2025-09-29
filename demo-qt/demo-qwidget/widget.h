#ifndef WIDGET_H
#define WIDGET_H

#include <QWidget>
#include "another_widget.h"

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
    // 跳转的窗口
    AnotherWidget *anotherWidget;
};
#endif // WIDGET_H
