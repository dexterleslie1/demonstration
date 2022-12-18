# coding:utf-8
import time

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions
from selenium.webdriver.support.wait import WebDriverWait


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


def test_explicitly_wait_element():
    driver = webdriver.Firefox()
    driver.get("https://www.baidu.com")
    wait = WebDriverWait(driver=driver, timeout=5)
    # https://blog.csdn.net/dingding_ting/article/details/116768566
    wait.until(expected_conditions.title_is("百度一下，你就知道"))

    # https://www.jb51.net/article/219686.htm
    wait.until(expected_conditions.presence_of_element_located((By.XPATH, "//input[@id='su']")))

    driver.quit()


if __name__ == "__main__":
    # test_implicitly_wait_element()
    test_explicitly_wait_element()
