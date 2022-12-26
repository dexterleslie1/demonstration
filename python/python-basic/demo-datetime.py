# coding:utf-8

import datetime

if __name__ == "__main__":
    # 获取当前系统时间
    timeNow = datetime.datetime.now()
    print("当前系统时间： ", timeNow)

    # 根据年月日创建时间对象
    datetimeObject = datetime.datetime(2022, 12, 1)
    print("手动创建的日期对象：", datetimeObject)

    # 时间转换为字符串
    datetimeString = timeNow.strftime("%Y-%m-%d %H:%M:%S")
    print("时间对象 ", timeNow, " 转换字符串后 ", datetimeString)

    # 字符串转换为时间
    datetimeObject = datetime.datetime.strptime(datetimeString, "%Y-%m-%d %H:%M:%S")
    print("字符串 ", datetimeString, " 转换时间对象后 ", datetimeObject)

    # 时间加一秒
    datetimeObject = timeNow + datetime.timedelta(seconds=1)
    print("时间 ", timeNow, " 加1秒后 ", datetimeObject)

    # 时间加一分钟
    datetimeObject = timeNow + datetime.timedelta(minutes=1)
    print("时间 ", timeNow, " 加1分钟后 ", datetimeObject)

    # 时间加一天
    datetimeObject = timeNow + datetime.timedelta(days=1)
    print("时间 ", timeNow, " 加1天后 ", datetimeObject)

    # 时间比较
    datetimeObject = timeNow + datetime.timedelta(days=1)
    if timeNow < datetimeObject:
        print("时间 ", timeNow, " 早于时间 ", datetimeObject)

    # 时间戳转换为datetime对象
    timestampObject = 1671217200000
    datetimeObject = datetime.datetime.fromtimestamp(timestampObject/1000)
    print("时间戳:", timestampObject, "转换为datetime对象:", datetimeObject)

    # 计算日期之间的天数
    # https://blog.csdn.net/weixin_41967600/article/details/126433017
    datetimeObject = timeNow + datetime.timedelta(days=-1)
    days = (timeNow - datetimeObject).days
    print("时间", timeNow, "和时间", datetimeObject, "相隔", days, "天")

    pass
