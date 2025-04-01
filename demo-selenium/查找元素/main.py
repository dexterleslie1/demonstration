# coding:utf-8

from selenium import webdriver
from selenium.webdriver.common.by import By
import time


# 测试根据id查找元素
def test_find_element_by_id():
    driver = webdriver.Firefox()
    driver.get("https://www.baidu.com")
    element = driver.find_element(By.ID, "kw")
    element.send_keys("测试\n")
    element = driver.find_element(By.ID, "su")
    element.click()

    time.sleep(2)
    driver.quit()
    pass


def test_find_element_by_class():
    driver = webdriver.Firefox()
    driver.get("https://www.baidu.com")
    # 获取符合classname的第一个元素
    element = driver.find_element(By.CLASS_NAME, "s_ipt")
    element.send_keys("测试\n")
    element = driver.find_element(By.ID, "su")
    element.click()

    time.sleep(2)
    driver.quit()
    pass


def test_find_element_by_class1():
    driver = webdriver.Firefox()
    driver.get("https://www.baidu.com")
    elements = driver.find_elements(By.CLASS_NAME, "s_ipt")
    elements[0].send_keys("测试\n")
    element = driver.find_element(By.ID, "su")
    element.click()

    time.sleep(2)
    driver.quit()
    pass


def test_find_elements_by_tag_name():
    driver = webdriver.Firefox()
    driver.get("https://www.baidu.com")
    elements = driver.find_elements(By.TAG_NAME, "span")
    print("tag为span的元素总数：" + str(len(elements)))
    element = driver.find_element(By.ID, "su")
    element.click()

    time.sleep(2)
    driver.quit()
    pass


def test_find_elements_by_css_selector():
    driver = webdriver.Firefox()
    driver.get("https://www.baidu.com")
    elements = driver.find_elements(By.CSS_SELECTOR, "input[name='wd']")
    elements[0].send_keys("测试\n")
    element = driver.find_element(By.ID, "su")
    element.click()

    time.sleep(2)
    driver.quit()
    pass


# 测试在指定的webelement范围查找元素
def test_find_element_by_css_selector_in_webelement_scope():
    driver = webdriver.Firefox()
    driver.get("https://www.baidu.com")
    element = driver.find_element(By.CLASS_NAME, "has-soutu")
    elements = element.find_elements(By.CSS_SELECTOR, "input[name='wd']")
    elements[0].send_keys("测试\n")
    element = driver.find_element(By.ID, "su")
    element.click()

    time.sleep(2)
    driver.quit()
    pass


if __name__ == "__main__":
    # test_find_element_by_id()
    # test_find_element_by_class()
    # test_find_element_by_class1()
    # test_find_elements_by_tag_name()
    # test_find_elements_by_css_selector()
    # test_find_element_by_css_selector_in_webelement_scope()
    test_find_element_by_css_selector_with_multiple_classname()
