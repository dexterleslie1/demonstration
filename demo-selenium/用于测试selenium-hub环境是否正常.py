import time

from selenium import webdriver

# NOTE: 这个文件用于测试selenium hub环境是否正常配置

# 启用无界面模式启动浏览器，否则报错：Failed to open connection to "session" message bus: Unable to autolaunch a dbus-daemon without a $DISPLAY for X11
# Running without a11y support! Error: no DISPLAY environment variable specified
# https://stackoverflow.com/questions/66892502/chromedriver-desired-capabilities-has-been-deprecated-please-pass-in-an-options
# DesiredCapabilities被抛弃，使用options
chrome_options = webdriver.ChromeOptions()
# 指定测试的浏览器版本
# https://stackoverflow.com/questions/63588424/chrome-browser-version-in-selenium
chrome_options.set_capability("browserVersion", "96.0")

chrome_options_without_version = webdriver.ChromeOptions()
firefox_options_without_version = webdriver.FirefoxOptions()
options_list = [chrome_options, chrome_options_without_version, firefox_options_without_version]
# 串行一个个浏览器执行测试
# https://gist.github.com/devinmancuso/54904c005f8d237f6fec
for options in options_list:
    # 连接本地 selenium grid hub
    varDriver = webdriver.Remote(command_executor="http://192.168.1.181:4444/wd/hub",
                                 options=options)
    varDriver.get("https://www.baidu.com")

    time.sleep(5)

    varDriver.quit()
