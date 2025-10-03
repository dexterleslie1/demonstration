#ifndef WIDGETBORDERWITHGROUPBOX_H
#define WIDGETBORDERWITHGROUPBOX_H

#include <QWidget>

namespace Ui {
class WidgetBorderWithGroupBox;
}

class WidgetBorderWithGroupBox : public QWidget
{
    Q_OBJECT

public:
    explicit WidgetBorderWithGroupBox(QWidget *parent = nullptr);
    ~WidgetBorderWithGroupBox();

private:
    Ui::WidgetBorderWithGroupBox *ui;
};

#endif // WIDGETBORDERWITHGROUPBOX_H
