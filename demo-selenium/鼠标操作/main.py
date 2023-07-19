# coding:utf-8
import time

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.support import expected_conditions
from selenium.webdriver.support.wait import WebDriverWait

if __name__ == "__main__":
    try:
        driver = webdriver.Firefox()
        driver.get("http://192.168.1.181/web-auxiliary-tool-for-testing/index.html")

        element = driver.find_element(By.CLASS_NAME, 'box3')

        #region 演示鼠标移动和点击

        # https://www.browserstack.com/guide/action-class-selenium-python
        action = ActionChains(driver)
        action.move_to_element(element)
        action.click().perform()
        # 等待alert框
        wait = WebDriverWait(driver, 1)
        alert = wait.until(expected_conditions.alert_is_present())
        alert.accept()

        #endregion

        #region 演示鼠标拖拽

        # https://www.browserstack.com/guide/drag-and-drop-in-selenium
        element = driver.find_element(By.CLASS_NAME, 'box5')
        action = ActionChains(driver)
        action.drag_and_drop_by_offset(element, 10, 10)
        action.perform()
        element = driver.find_element(By.CLASS_NAME, 'box52')
        inner_html = element.get_attribute('innerHTML')
        assert 'dragging...' == inner_html

        element = driver.find_element(By.TAG_NAME, 'body')
        action = ActionChains(driver)
        action.drag_and_drop_by_offset(element, 134, 134)
        action.perform()
        element = driver.find_element(By.CLASS_NAME, 'box52')
        inner_html = element.get_attribute('innerHTML')
        assert '' == inner_html

        #endregion

    finally:
        driver.quit()
pass
