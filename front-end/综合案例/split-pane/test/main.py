# coding:utf-8
import math
import time

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains


def drag_and_drop_splitter(offsetX, offsetY):
    element_container = driver.find_element(By.ID, 'my-lr-split-pane')

    element = element_container.find_element(By.CLASS_NAME, 'splitter')
    element_left_pane = element_container.find_element(By.CLASS_NAME, 'left-pane')

    locationOriginal = element.location
    sizeOriginal = element.size
    leftPaneLocationOriginal = element_left_pane.location
    leftPaneSizeOriginal = element_left_pane.size

    action = ActionChains(driver)
    action.drag_and_drop_by_offset(element, offsetX, offsetY)
    action.perform()

    locationNew = element.location
    sizeNew = element.size
    leftPaneLocationNew = element_left_pane.location
    leftPaneSizeNew = element_left_pane.size

    return (locationOriginal, locationNew, sizeOriginal, sizeNew,
            leftPaneLocationOriginal, leftPaneLocationNew, leftPaneSizeOriginal, leftPaneSizeNew)

    pass


if __name__ == "__main__":
    try:
        driver = webdriver.Firefox()
        driver.get("http://127.0.0.1:5501/%E7%BB%BC%E5%90%88%E6%A1%88%E4%BE%8B/split-pane/index.html")

        # region splitter拖拽鼠标

        # 测试正常拖拽
        offsetX = -11
        offsetY = -12
        (locationOriginal, locationNew, sizeOriginal, sizeNew,
         leftPaneLocationOriginal, leftPaneLocationNew, leftPaneSizeOriginal, leftPaneSizeNew) \
            = drag_and_drop_splitter(offsetX, offsetY)
        assert offsetX == locationNew['x'] - locationOriginal['x']
        assert 0 == locationNew['y'] - locationOriginal['y']
        assert sizeOriginal['width'] == sizeNew['width']
        assert sizeOriginal['height'] == sizeNew['height']
        assert leftPaneLocationOriginal['x'] == leftPaneLocationNew['x']
        assert leftPaneLocationOriginal['y'] == leftPaneLocationNew['y']
        assert math.ceil(leftPaneSizeOriginal['width']) + offsetX == leftPaneSizeNew['width']
        assert leftPaneSizeOriginal['height'] == leftPaneSizeNew['height']
        offsetX = 45
        offsetY = 66
        (locationOriginal, locationNew, sizeOriginal, sizeNew,
         leftPaneLocationOriginal, leftPaneLocationNew, leftPaneSizeOriginal, leftPaneSizeNew) \
            = drag_and_drop_splitter(offsetX, offsetY)
        assert offsetX == locationNew['x'] - locationOriginal['x']
        assert 0 == locationNew['y'] - locationOriginal['y']
        assert sizeOriginal['width'] == sizeNew['width']
        assert sizeOriginal['height'] == sizeNew['height']
        assert leftPaneLocationOriginal['x'] == leftPaneLocationNew['x']
        assert leftPaneLocationOriginal['y'] == leftPaneLocationNew['y']
        assert math.ceil(leftPaneSizeOriginal['width']) + offsetX == leftPaneSizeNew['width']
        assert leftPaneSizeOriginal['height'] == leftPaneSizeNew['height']

        # 测试拖拽超出min-width
        offsetX = -270
        offsetY = -300
        (locationOriginal, locationNew, sizeOriginal, sizeNew,
         leftPaneLocationOriginal, leftPaneLocationNew, leftPaneSizeOriginal, leftPaneSizeNew) \
            = drag_and_drop_splitter(offsetX, offsetY)
        assert 200 == locationNew['x']
        assert 0 == locationNew['y'] - locationOriginal['y']
        assert sizeOriginal['width'] == sizeNew['width']
        assert sizeOriginal['height'] == sizeNew['height']
        assert leftPaneLocationOriginal['x'] == leftPaneLocationNew['x']
        assert leftPaneLocationOriginal['y'] == leftPaneLocationNew['y']
        assert 200 == leftPaneSizeNew['width']
        assert leftPaneSizeOriginal['height'] == leftPaneSizeNew['height']

        # endregion

        # region 测试leftPane折叠和展开

        element_container = driver.find_element(By.ID, 'my-lr-split-pane')

        element = element_container.find_element(By.CLASS_NAME, 'splitter')
        size_splitter_original = element.size
        element = element_container.find_element(By.CLASS_NAME, 'left-pane')
        size_left_pane_original = element.size

        # 测试折叠
        element = element_container.find_element(By.CLASS_NAME, 'btn-l-pane-collapse-expand-toggle')
        element.click()

        # 验证leftPane location和size
        element = element_container.find_element(By.CLASS_NAME, 'left-pane')
        location = element.location
        assert 0 == location['x']
        assert 0 == location['y']
        size = element.size
        assert 0 == size['width']
        assert 0 == size['height']
        displayed = element.is_displayed()
        assert not displayed

        # 验证splitter location和size
        element = driver.find_element(By.CLASS_NAME, 'splitter')
        location = element.location
        assert 0 == location['x']
        assert 0 == location['y']
        size = element.size
        assert 25 == size_splitter_original['width']
        assert size_splitter_original['width'] == size['width']
        assert size_splitter_original['height'] == size['height']
        element = element_container.find_element(By.CLASS_NAME, 'btn-l-pane-collapse-expand-toggle')
        inner_html = element.get_attribute('innerHTML')
        assert inner_html == '&gt;'

        # 测试展开
        element = element_container.find_element(By.CLASS_NAME, 'btn-l-pane-collapse-expand-toggle')
        element.click()

        element = element_container.find_element(By.CLASS_NAME, 'left-pane')
        location = element.location
        assert 0 == location['x']
        assert 0 == location['y']
        size = element.size
        assert size_left_pane_original['width'] == size['width']
        assert size_left_pane_original['height'] == size['height']
        displayed = element.is_displayed()
        assert displayed
        element = element_container.find_element(By.CLASS_NAME, 'btn-l-pane-collapse-expand-toggle')
        inner_html = element.get_attribute('innerHTML')
        assert inner_html == '&lt;'

        # endregion

    finally:
        driver.quit()
pass
