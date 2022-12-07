from selenium import webdriver

# 启用无界面模式启动浏览器，否则报错：Failed to open connection to "session" message bus: Unable to autolaunch a dbus-daemon without a $DISPLAY for X11
# Running without a11y support! Error: no DISPLAY environment variable specified
# https://stackoverflow.com/questions/66892502/chromedriver-desired-capabilities-has-been-deprecated-please-pass-in-an-options
# DesiredCapabilities被抛弃，使用options
varOptions = webdriver.FirefoxOptions()
varOptions.add_argument("--headless")
varDriver = webdriver.Remote(command_executor="http://192.168.1.111:4444/wd/hub",
                             options=varOptions)
varDriver.get("https://www.baidu.com")
input()
varDriver.quit()