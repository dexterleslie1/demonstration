from selenium import webdriver

if __name__ == "__main__":
    # 创建控制浏览器对象
    varDriver = webdriver.Firefox()
    varDriver.get("https://www.baidu.com")
    input()
    varDriver.quit()
