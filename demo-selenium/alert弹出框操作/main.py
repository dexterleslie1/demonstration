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
        location = element.location
        size = element.size

        # https://www.browserstack.com/guide/action-class-selenium-python
        action = ActionChains(driver)
        action.move_to_element(element)
        action.click().perform()

        # 等待alert框
        # https://stackoverflow.com/questions/11467471/how-to-check-if-an-alert-exists-using-webdriver
        wait = WebDriverWait(driver, 1)
        wait.until(expected_conditions.alert_is_present())

        # 断言弹出框内容
        # https://stackoverflow.com/questions/46101336/webdriver-switchto-alert-function-fails
        # https://stackoverflow.com/questions/47074558/how-to-read-the-text-from-the-alert-box-using-python-selenium
        alert = driver.switch_to.alert
        alert_text = alert.text
        expected_text = 'x=' + str(location['x']) + ',y=' + str(location['y']) + ',width=' + str(int(size['width'])) + ',height=' + str(int(size['height']))
        assert expected_text == alert_text
        alert.accept()

    finally:
        driver.quit()
pass
