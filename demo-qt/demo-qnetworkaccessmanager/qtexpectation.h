#ifndef QTEXPECTATION_H
#define QTEXPECTATION_H

#include <QObject>
#include <QEventLoop>
#include <QTimer>

class QtExpectation : public QObject {
    Q_OBJECT

public:
    explicit QtExpectation(int timeout = 3000, QObject *parent = nullptr)
        : QObject(parent), m_timeout(timeout), m_fulfilled(false) {}

    void fulfill() {
        m_fulfilled = true;
        emit fulfilled();
    }

    bool wait() {
        QEventLoop loop;
        QTimer::singleShot(m_timeout, &loop, &QEventLoop::quit);
        connect(this, &QtExpectation::fulfilled, &loop, &QEventLoop::quit);
        loop.exec();
        return m_fulfilled;
    }

    bool isFulfilled() const { return m_fulfilled; }

signals:
    void fulfilled();

private:
    int m_timeout;
    bool m_fulfilled;
};

#endif // QTEXPECTATION_H
