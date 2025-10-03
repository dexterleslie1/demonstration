#ifndef WIDGETBORDERWITHOUTGROUPBOX_H
#define WIDGETBORDERWITHOUTGROUPBOX_H

#include <QWidget>

namespace Ui {
class WidgetBorderWithoutGroupBox;
}

class WidgetBorderWithoutGroupBox : public QWidget
{
    Q_OBJECT

public:
    explicit WidgetBorderWithoutGroupBox(QWidget *parent = nullptr);
    ~WidgetBorderWithoutGroupBox();

private:
    Ui::WidgetBorderWithoutGroupBox *ui;
};

#endif // WIDGETBORDERWITHOUTGROUPBOX_H
