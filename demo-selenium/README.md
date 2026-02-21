## 工作原理

Selenium是一个用于Web应用程序自动化测试的工具，其原理涉及多个组件和步骤。以下是Selenium工作原理的详细解释：

一、核心组件

1. **Selenium WebDriver**：
   - 是Selenium的核心组件，允许程序员编写自动化测试脚本。
   - 通过WebDriver API与浏览器进行交互，能够直接控制浏览器，模拟用户操作，如点击、输入文本、导航页面等。
2. **浏览器驱动程序**：
   - Selenium需要与不同浏览器的驱动程序进行通信，这些驱动程序是实现与实际浏览器交互的桥梁。
   - 例如，Chrome浏览器需要ChromeDriver，Firefox浏览器需要geckodriver，Edge浏览器需要EdgeDriver等。
   - 浏览器驱动程序与浏览器通过原生的浏览器协议或W3C WebDriver协议进行通信。
3. **浏览器**：
   - Selenium支持多种类型的浏览器，包括Chrome、Firefox、Safari、Edge等。
   - 浏览器接收来自WebDriver的执行命令，完成相应的操作。

二、工作原理

1. **初始化驱动**：
   - 根据需要选择并启动特定浏览器的驱动。例如，如果要使用Chrome浏览器进行自动化测试，则需要启动ChromeDriver。
2. **发送命令**：
   - 通过WebDriver API，Selenium发送操作指令给浏览器驱动。这些指令包括定位元素、执行操作（如点击、输入、获取文本等）等。
3. **浏览器执行**：
   - 浏览器驱动接收来自WebDriver的指令，并通过与浏览器内部的原生自动化接口（如Chrome DevTools Protocol）进行交互，执行指令。
4. **返回结果**：
   - 操作完成后，浏览器驱动将执行结果返回给WebDriver。Selenium然后可以根据返回的结果继续执行下一步操作。

三、通信方式

WebDriver与浏览器驱动之间通过HTTP协议进行通信，通常是JSON over HTTP。WebDriver使用命令（如GET、POST）与浏览器驱动通信，浏览器驱动再将这些命令翻译成浏览器可以理解的操作。

四、元素定位与操作

1. **元素定位**：
   - Selenium使用多种方式定位网页中的元素，包括ID、Name、ClassName、TagName、XPath等。
   - 一旦定位到元素，WebDriver可以对其执行操作，如点击、输入、获取文本等。
2. **等待操作**：
   - Selenium支持显式等待和隐式等待，用于确保页面的元素加载完成后再进行操作。
   - 显式等待会等待某个条件（如元素是否可见）成立，而隐式等待则会在每次查找元素时设置一个最大等待时间。

五、无头模式

Selenium还支持无头模式（headless），这种模式下，浏览器不会显示图形界面，但依然会执行页面操作。无头模式通常用于自动化测试、爬虫等不需要与用户直接交互的场景。

综上所述，Selenium的工作原理是通过WebDriver与浏览器驱动交互，模拟用户行为，自动化操作网页并获取测试结果。



## 兼容性

selenium、driver、browser兼容性，firefox兼容性`https://firefox-source-docs.mozilla.org/testing/geckodriver/Support.html`

## hub和非hub模式是什么呢？

Selenium Hub 模式和非 Hub 模式是 Selenium Grid 架构中的两种部署方式，主要区别在于**测试脚本执行节点的管理方式**。

------

### 1. **非 Hub 模式（Standalone 模式）**

- **定义**：直接在本地机器上启动浏览器驱动（如 ChromeDriver、GeckoDriver），测试脚本与浏览器驱动一对一通信。

- **架构**：

  ```mermaid
  graph LR
    A[测试脚本] --> B[浏览器驱动]
    B --> C[本地浏览器]
  ```

- **特点**：

  - **简单**：无需额外配置，适合本地调试和快速执行。
  - **单机**：只能使用本机的浏览器，无法跨平台或并行执行。

- **适用场景**：个人开发调试、小规模测试、不需要并行的场景。

------

### 2. **Hub 模式（Grid 模式）**

- **定义**：基于 Selenium Grid 架构，由 **Hub（中心节点）** 统一接收测试请求，并分发到 **Node（执行节点）** 执行。

- **架构**：

  ```mermaid
  graph LR
    A[测试脚本] --> H[Hub中心]
    H --> N1[Node 1]
    H --> N2[Node 2]
    N1 --> B1[浏览器]
    N2 --> B2[浏览器]
  ```

- **特点**：

  - **集中管理**：Hub 负责分配任务，支持负载均衡。
  - **跨平台**：Node 可注册到 Hub，支持不同操作系统和浏览器版本。
  - **并行执行**：多个测试用例可同时分发到不同 Node 执行。

- **适用场景**：持续集成（CI）、大规模测试、需要兼容性测试（多浏览器/多系统）。

------

### 3. **核心区别对比**

| 特性           | 非 Hub 模式      | Hub 模式           |
| -------------- | ---------------- | ------------------ |
| **架构复杂度** | 低（单机）       | 高（分布式）       |
| **并行能力**   | 不支持（单线程） | 支持（多节点并行） |
| **跨平台**     | 仅限本地环境     | 支持多种 OS/浏览器 |
| **维护成本**   | 低               | 需维护 Hub 和 Node |
| **典型用途**   | 开发调试         | CI/CD、回归测试    |

------

### 4. **如何选择？**

- **选非 Hub 模式**：
  - 只需在本地快速验证功能。
  - 团队规模小，无需复杂环境。
- **选 Hub 模式**：
  - 需要同时测试多种浏览器（如 Chrome、Firefox、Safari）。
  - 测试用例多，需要缩短执行时间。
  - 集成到 Jenkins/GitLab CI 等流水线中。

**注意**：Selenium 4 已默认支持 Standalone 模式（内置 Hub 和 Node），但生产环境仍建议使用独立 Grid 部署。

## ubuntu 配置 selenium

### 非 hub 模式

查看本地 firefox 浏览器版本，本示例使用 firefox-v133.0(64-bit)

配置 geckodriver 驱动

>`https://askubuntu.com/questions/870530/how-to-install-geckodriver-in-ubuntu`

- 通过兼容性列表查看需要下载 geckodriver-v0.35.0-linux64.tar.gz，下载地址为`https://github.com/mozilla/geckodriver/releases`

- 解压驱动到 /usr/local/bin 目录

  ```bash
  sudo tar -xvzf geckodriver-v0.35.0-linux64.tar.gz -C /usr/local/bin && sudo chown root:root /usr/local/bin/geckodriver && sudo chmod +x /usr/local/bin/geckodriver
  ```

- 测试驱动是否成功配置

  ```bash
  geckodriver --version
  ```

安装 selenium

```bash
sudo pip3 install selenium==4.0.0
```

测试环境是否配置正常

- 新建 main.py 文件内容如下：

  ```python
  # coding=utf-8
  
  from selenium import webdriver
  
  if __name__ == "__main__":
      # 创建控制浏览器对象
      varDriver = webdriver.Firefox()
      varDriver.get("https://www.baidu.com")
      input()
      varDriver.quit()
  
  ```

- 运行 main.py 文件

  ```bash
  python3 main.py
  ```

  - 如果弹出 firefox 浏览器说明环境配置正常



### hub 模式

注意：未做实验。

下载 grid standalone，注意：此次下载版本 3.141.59，`https://github.com/SeleniumHQ/selenium/releases`

启动 hub 和 node `https://www.selenium.dev/documentation/legacy/selenium_3/grid_setup/`

```shell
# 启动hub
java -jar selenium-server-standalone-3.141.59.jar -role hub

# 启动node加入hub后，配置参考dcli selenium脚本手动配置浏览器驱动到/usr/bin目录
java -jar selenium-server-standalone-3.141.59.jar -role node -hub http://192.168.1.111:4444
```



## 边测试边录制视频（Docker）

在基于 Docker 运行 Selenium 时，可同时启动视频录制服务，将测试过程中的浏览器画面保存为视频，便于回放与排查问题。

**重要**：官方 video 容器通过 **事件总线（ZeroMQ）** 接收“会话创建/关闭”事件才会开始/结束录制；**Standalone 容器不暴露事件总线**，因此仅用 `docker-compose.yml`（Standalone）时无法录制。需使用 **Hub + Node + Video** 的 compose 才能正常出片。

### 启用方式

1. **使用带录制的 compose**  
   在 `demo-selenium` 目录下使用 `docker-compose-hub.yml`（内含 Hub、Firefox Node、Video 三个服务）：

   ```bash
   docker compose -f docker-compose-hub.yml up -d
   ```
   视频会写入当前目录下的 `videos` 文件夹。

2. **运行测试**  
   测试仍连接 `http://localhost:4444/wd/hub`（连到 Hub），无需改 URL 录制会自动进行。

3. **测试端正常关闭**  
   
   会话结束时请使用 `driver.quit()` 正常关闭，以便录制服务生成并落盘视频文件。
   
4. **查看录制结果**  
   视频文件会出现在当前目录下的 `videos` 目录。文件名由录制服务自动生成（如带 session 信息），可在该目录下按时间或文件名查找对应测试会话的录像。

### 与默认 compose 的区别

- `docker-compose.yml`：仅 Standalone Firefox，**不支持**视频录制（无事件总线）。
- `docker-compose-hub.yml`：Hub + Firefox Node + Video，**支持**边测试边录制；测试仍访问 4444 端口。

### 注意

- 录制依赖 [selenium/video](https://github.com/SeleniumHQ/docker-selenium) 镜像，通过 FFmpeg 捕获浏览器容器画面。
- 确保 `./videos` 目录存在或 Docker 能创建该目录，否则挂载可能报错。

---

## 基于 docker 配置 selenium

### 非 hub 模式

>`https://hub.docker.com/r/selenium/standalone-firefox`

安装 selenium

```bash
sudo pip3 install selenium==4.0.0
```

docker compose 文件，打开浏览器查看 noVNC `http://127.0.0.1:7900/` 默认密码：secret

```yaml
version: "3.0"

# https://hub.docker.com/r/selenium/standalone-firefox
services:
  demo-firefox:
    image: selenium/standalone-firefox:latest
    # ports:
    #   - "4444:4444"
    #   - "7900:7900"
    network_mode: host

```

测试环境是否正常

- main.py 内容如下：

  ```python
  import time
  from selenium import webdriver
  
  firefox_options = webdriver.FirefoxOptions()
  varDriver = webdriver.Remote(command_executor="http://localhost:4444/wd/hub",
                                  options=firefox_options)
  varDriver.get("https://www.baidu.com")
  
  time.sleep(15)
  
  varDriver.quit()
  ```

- 运行测试

  ```bash
  python3 main.py
  ```



### hub 模式

>`https://www.hduzn.cn/2022/05/29/Docker%E7%89%88Selenium%E4%BD%BF%E7%94%A8%E6%96%B9%E6%B3%95/`
>
>目前只支持运行 chrome 和 firefox 节点。todo 寻找方案支持 IE、safari 节点

安装 selenium

```bash
sudo pip3 install selenium==4.0.0
```

docker compose 文件，打开浏览器查看 noVNC `http://127.0.0.1:7900/` 默认密码：secret

```yaml
version: "3.0"

services:
  # chrome节点
  demo-chrome:
    image: selenium/node-chrome:latest
    depends_on:
      - demo-hub
    environment:
      - SE_EVENT_BUS_HOST=demo-hub
      - SE_EVENT_BUS_PUBLISH_PORT=4442
      - SE_EVENT_BUS_SUBSCRIBE_PORT=4443
    ports:
      # VNC客户端连接端口
      - "5900:5900"
      # noVNC连接端口
      # 打开浏览器查看noVNC http://127.0.0.1:7900/ 默认密码: secret
      - "7900:7900"

  # chrome-v96.0 节点
  demo-chrome-960:
    image: selenium/node-chrome:96.0
    depends_on:
      - demo-hub
    environment:
      - SE_EVENT_BUS_HOST=demo-hub
      - SE_EVENT_BUS_PUBLISH_PORT=4442
      - SE_EVENT_BUS_SUBSCRIBE_PORT=4443
    ports:
      - "5902:5900"
      - "7902:7900"

  # firefox节点
  demo-firefox:
    image: selenium/node-firefox:latest
    depends_on:
      - demo-hub
    environment:
      - SE_EVENT_BUS_HOST=demo-hub
      - SE_EVENT_BUS_PUBLISH_PORT=4442
      - SE_EVENT_BUS_SUBSCRIBE_PORT=4443
    ports:
      - "5901:5900"
      - "7901:7900"

  # selenium grid hub
  demo-hub:
    image: selenium/hub:latest
    ports:
      - "4442:4442"
      - "4443:4443"
      # 查看 selenium hub状态 http://127.0.0.1:4444/
      - "4444:4444"
```

访问`http://127.0.0.1:4444/`查看 hub 状态

测试环境是否正常

- main.py 内容如下：

  ```python
  # import time
  # from selenium import webdriver
  
  # firefox_options = webdriver.FirefoxOptions()
  # varDriver = webdriver.Remote(command_executor="http://localhost:4444/wd/hub",
  #                                 options=firefox_options)
  # varDriver.get("https://www.baidu.com")
  
  # time.sleep(15)
  
  # varDriver.quit()
  
  import time
  
  from selenium import webdriver
  
  # NOTE: 这个文件用于测试selenium hub环境是否正常配置
  
  # 启用无界面模式启动浏览器，否则报错：Failed to open connection to "session" message bus: Unable to autolaunch a dbus-daemon without a $DISPLAY for X11
  # Running without a11y support! Error: no DISPLAY environment variable specified
  # https://stackoverflow.com/questions/66892502/chromedriver-desired-capabilities-has-been-deprecated-please-pass-in-an-options
  # DesiredCapabilities被抛弃，使用options
  chrome_options = webdriver.ChromeOptions()
  # 指定测试的浏览器版本
  # https://stackoverflow.com/questions/63588424/chrome-browser-version-in-selenium
  chrome_options.set_capability("browserVersion", "96.0")
  
  chrome_options_without_version = webdriver.ChromeOptions()
  firefox_options_without_version = webdriver.FirefoxOptions()
  options_list = [chrome_options, chrome_options_without_version, firefox_options_without_version]
  # 串行一个个浏览器执行测试
  # https://gist.github.com/devinmancuso/54904c005f8d237f6fec
  for options in options_list:
      # 连接本地 selenium grid hub
      varDriver = webdriver.Remote(command_executor="http://localhost:4444/wd/hub",
                                   options=options)
      varDriver.get("https://www.baidu.com")
  
      time.sleep(5)
  
      varDriver.quit()
  
  
  ```

- 运行测试

  ```bash
  python3 main.py
  ```



## Java 使用 Selenium

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-selenium/demo-java`



### SpringBoot 项目集成 Selenium Java 客户端驱动

SpringBoot 项目 pom 依赖配置引用如下配置：

```xml
<!-- selenium 依赖 -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
</dependency>
```



### 创建和销毁 WebDriver 实例

>`https://medium.com/@riddhipandya153/how-to-build-a-selenium-maven-project-86394f2d70e1`

```java
// 测试 WebDriver 创建和销毁
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebDriverCreationAndDestroyTests {
    WebDriver driver;

    @Before
    public void before() throws MalformedURLException {
        // 创建 WebDriver 实例
        driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), new FirefoxOptions());
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    @After
    public void after() {
        if (driver != null) {
            // 关闭浏览器
            driver.quit();
        }
    }

    @Test
    public void test() throws InterruptedException {
        driver.get("http://localhost:8080");
        TimeUnit.SECONDS.sleep(5);
    }
}
```



### implicitlyWait

mplicitlyWait() 设置一个隐式等待时间。 Selenium 会在查找元素时，自动等待最长指定的时间。 如果元素在等待时间内出现，则测试继续；如果元素在等待时间内未出现，则抛出异常。 

```java
// 测试 implicitlyWait timeout
@Test
public void testImplicitlyWait() throws InterruptedException {
    driver.get("http://localhost:8080");

    // implicitlyWait() 设置一个隐式等待时间。 Selenium 会在查找元素时，自动等待最长指定的时间。 如果元素在等待时间内出现，则测试继续；如果元素在等待时间内未出现，则抛出异常。 
    driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    try {
        driver.findElement(By.id(UUID.randomUUID().toString()));
        Assert.fail();
    } catch (NoSuchElementException ignored) {

    }
    stopWatch.stop();
    double seconds = stopWatch.getTotalTimeSeconds();
    Assert.assertTrue(seconds >= 15);
}
```



### 切换 iframe

>`https://www.selenium.dev/documentation/webdriver/interactions/frames/`

```java
// 测试切换 iframe 并查找元素
@Test
public void testSwitchToIframeAndFindElements() throws InterruptedException {
    driver.get("http://localhost:8080");

    // 切换到 iframe 上下文
    WebElement iframe1 = driver.findElement(By.id("iframe1"));
    driver.switchTo().frame(iframe1);

    // 在 iframe 中查找元素并点击
    List<WebElement> elementList = driver.findElements(By.cssSelector("[class=\"ntes-nav-index-title ntes-nav-entry-wide c-fl\"]"));
    elementList.get(0).click();

    TimeUnit.SECONDS.sleep(5);
}
```



### 非 xpath 选择器



#### 据多个 css class 查询元素

```java
// 测试根据多个 css class 查询元素
@Test
public void testFindElementByUsingCssSelectorWithMultipleClassNames() throws InterruptedException {
    driver.get("https://www.baidu.com");
    List<WebElement> elementList = driver.findElements(By.cssSelector("[class=\"mnav c-font-normal c-color-t\"]"));
    elementList.get(0).click();

    TimeUnit.SECONDS.sleep(2);
}
```



### xpath 选择器

#### 根据 class 和元素 innerHTML 定位元素

>`https://stackoverflow.com/questions/26370554/how-to-locate-an-element-by-class-name-and-its-text-in-python-selenium`

```java
// 测试根据 class 和元素 innerHTML 定位元素
// https://stackoverflow.com/questions/26370554/how-to-locate-an-element-by-class-name-and-its-text-in-python-selenium
@Test
public void testFindElementByUsingXPathWithClassNameAndInnerHTML() throws InterruptedException {
    driver.get("http://localhost:8080");

    WebElement element = driver.findElement(By.xpath("//span[@class='c-text' and text()='早']"));
    Assert.assertNotNull(element);
}
```
