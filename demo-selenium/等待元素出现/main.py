# coding:utf-8
import time

from selenium import webdriver
from selenium.webdriver.common.by import By


def test_implicitly_wait_element():
    driver = webdriver.Firefox()
    # 使用隐式等待元素出现，最长等待时间10秒，否则id=3001元素查找报错NoSuchElement
    driver.implicitly_wait(10)
    driver.get("https://www.baidu.com")
    element = driver.find_element(By.CLASS_NAME, "has-soutu")
    elements = element.find_elements(By.CSS_SELECTOR, "input[name='wd']")
    elements[0].send_keys("测试\n")
    element = driver.find_element(By.ID, "su")
    element.click()

    element = driver.find_element(By.ID, "3001")
    print(element.text)

    time.sleep(2)
    driver.quit()
    pass


if __name__ == "__main__":
    test_implicitly_wait_element()
