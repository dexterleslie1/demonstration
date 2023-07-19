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
        driver.get("http://127.0.0.1:5500/web-auxiliary-tool-for-testing/index.html")

        element = driver.find_element(By.CLASS_NAME, 'box3')

        # https://www.browserstack.com/guide/action-class-selenium-python
        action = ActionChains(driver)
        action.move_to_element(element)
        action.click().perform()

        # 等待alert框
        wait = WebDriverWait(driver, 1)
        wait.until(expected_conditions.alert_is_present())

    finally:
        driver.quit()
pass
