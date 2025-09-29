#ifndef ANOTHER_WIDGET_H
#define ANOTHER_WIDGET_H

#include <QWidget>

namespace Ui {
class AnotherWidget;
}

class AnotherWidget : public QWidget
{
    Q_OBJECT

public:
    explicit AnotherWidget(QWidget *parent = nullptr);
    ~AnotherWidget();

private:
    Ui::AnotherWidget *ui;

signals:
    // 返回信号
    void back();
};

#endif // ANOTHER_WIDGET_H
