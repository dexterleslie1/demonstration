# selenium使用

## selenium工作原理

运行代码，启动浏览器后，webdriver会将浏览器绑定到特定端口，作为webdriver的remote server；Client（也就是测试脚本）借助ComandExecutor创建sessionId，发送HTTP请求（包括HTTP method, body）给remote server；remote server收到HTTP请求后，调用webdriver完成操作，并将HTTP响应的结果返回给Client。

## selenium环境设置

### selenium设置

> NOTE：使用dcli selenium install配置环境，目前只支持firefox selenium环境配置

**selenium、driver、browser兼容性**

 [firefox兼容性](https://firefox-source-docs.mozilla.org/testing/geckodriver/Support.html)

**浏览器驱动下载**

[firefox驱动下载地址](https://github.com/mozilla/geckodriver/releases)

### selenium grid设置

> todo 使用docker启动selenium grid

* 下载selenium grid standalone(NOTE: 此次下载版本3.141.59)
https://github.com/SeleniumHQ/selenium/releases

* 启动hub和node
https://www.selenium.dev/documentation/legacy/selenium_3/grid_setup/

```shell
# 启动hub
java -jar selenium-server-standalone-3.141.59.jar -role hub

# 启动node加入hub后，配置参考dcli selenium脚本手动配置浏览器驱动到/usr/bin目录
java -jar selenium-server-standalone-3.141.59.jar -role node -hub http://192.168.1.111:4444
```