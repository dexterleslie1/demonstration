#ifndef PAGE1_H
#define PAGE1_H

#include <QWidget>

namespace Ui {
class Page1;
}

class Page1 : public QWidget
{
    Q_OBJECT

public:
    explicit Page1(QWidget *parent = nullptr);
    ~Page1();

private:
    Ui::Page1 *ui;
};

#endif // PAGE1_H
