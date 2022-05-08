from selenium import webdriver

varDriver = webdriver.Firefox()
varDriver.get("https://www.baidu.com")
input()
varDriver.quit()
