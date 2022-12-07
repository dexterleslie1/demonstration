# coding:utf-8

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver import FirefoxOptions

if __name__ == "__main__":
    options = FirefoxOptions()
    options.headless = True
    driver = webdriver.Firefox(options=options)
    driver.get("https://www.baidu.com")

    element = driver.find_element(By.ID, "su")
    print(element.get_attribute("value"))

    driver.quit()
    pass
