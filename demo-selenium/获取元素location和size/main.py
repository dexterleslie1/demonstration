# coding:utf-8

from selenium import webdriver
from selenium.webdriver.common.by import By

if __name__ == "__main__":
    try:
        driver = webdriver.Firefox()
        driver.get("http://192.168.1.181/web-auxiliary-tool-for-testing/index.html")

        element = driver.find_element(By.CLASS_NAME, "box1")

        # 获取元素location
        # https://stackoverflow.com/questions/15510882/selenium-get-coordinates-or-dimensions-of-element-with-python
        location = element.location
        assert 300 == location['x']
        assert 150 == location['y']

        # 获取元素size
        # https://stackoverflow.com/questions/15510882/selenium-get-coordinates-or-dimensions-of-element-with-python
        size = element.size
        assert 50 == size['width']
        assert 50 == size['height']

        element = driver.find_element(By.CLASS_NAME, 'box3')
        location = element.location
        assert 8 == location['x']
        assert 134 == location['y']
        size = element.size
        assert 50 == size['width']
        assert 50 == size['height']

    finally:
        driver.quit()
pass
