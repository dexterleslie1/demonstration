## ~~`chromium`安装`SwitchyOmega`插件~~

>提示：
>
>- 2025年12月31日不能安装此插件，安装时报告错误说“清单不受支持”，可能是新版的Chrome浏览器不支持旧版本的插件。
>- 安装SmartProxy插件。

- 下载`SwitchyOmega`插件，`https://github.com/FelisCatus/SwitchyOmega/releases`

- 安装插件

  直接安装`crx`会在安装过程出现`CRX_HEADER_INVALID`错误，参考链接`https://blog.csdn.net/u014061630/article/details/89715656`

  参考以上链接方案二：把`crx`修改为扩展名`zip`，拖动`zip`到`chrome`地址栏`chrome://extensions/`中直接安装。如果拖动`zip`不能自动安装插件则在解压插件`zip`包，然后在插件管理界面打开开发者模式后点击`Load Unpacked`按钮选择解压后的插件目录。

- 配置完成`SwitchyOmega`后点击“应用选项”保存设置才能够生效

## 安装SmartProxy插件代理

访问 https://github.com/salarcode/SmartProxy/releases 下载插件

解压插件，使用谷歌浏览器访问chrome://extensions/，打开开发者模式，点击`加载未打包的扩展程序`按钮选择插件所在的目录就会自动安装插件

打开插件的设置页面，填写代理服务器的相关信息，代理协议填写`SOCKS5`并勾选`使用SOCKS5时代理DNS`。

## 通过第三方网站下载Chrome插件

访问 https://crxdl.com 搜索插件名称，点击下载zip压缩包，解压后zip压缩包后，拖拽crx文件到chrome://extensions/界面中（开启开发者模式）即可安装。

## Save All Resources插件

安装：访问 https://crxdl.com 下载Save All Resources插件（ID为abpdnfjocnmdomablahdcfnoggeeiedb），解压zip后推拽crx文件到chrome://extensions/界面中（开启开发者模式）即可安装。

使用：访问 https://www.daziya.com/，打开开发者工具切换到ResourcesSaver页签点击“Save All Resources”按钮就会自动打包下载网站为zip压缩包。

## `Ubuntu`安装`google chrome`浏览器

> 注意：使用`sudo snap install chromium`安装的浏览器在使用过程中有莫名奇妙的证书域名错误，所以放弃使用`chromium`浏览器。

- 到`google chrome`官方网站下载`deb`包，在国外访问 https://www.google.com/chrome，在国内访问 https://www.google.cn/intl/zh-CN_ALL/chrome/fallback/

- 使用`dpkg`安装`google chrome`

  ```bash
  sudo dpkg -i ~/Downloads/google-chrome-stable_current_amd64.deb
  ```



## `Windows11`安装`google chrome`浏览器

访问 https://www.google.cn/intl/zh-CN_ALL/chrome/fallback/ 下载安装程序并根据提示安装即可。



## Chrome版本144 beta版本下载

访问 https://www.google.com/chrome/beta/ 下载最新的Chrome beta版本。



## 借助`chrome`判断网站内容是否为`javascript`动态生成

原理：通过启用和禁用`javascript`运行判断网站内容是否为动态生成的。

- 打开`chrome`开发者调试工具
- 使用组合键`ctrl+shift+p`调出命令窗口，输入命令`disable javascript`后按`enter`键用于禁用`javascript`，输入命令`enable javascript`后按`enter`键用于启用`javascript`
- 通过禁用和启用`javascript`切换观察网站页面是否隐藏和显示部分内容判断网站内容是否为动态生成的



## 删除浏览数据

>提示：删除历史记录、`Cookie`、缓存内容及其他数据。

打开`设置` > `隐私与安全` > `删除浏览数据`功能，根据提示删除相应的数据即可。

## 问题1

描述：在 Chrome 开发者工具的“网络”标签页中，尤其是在启用“保留日志”选项后，如果出现“无法加载响应数据：找不到具有给定标识符的资源”的错误，通常意味着： 根据 Chrome 的“低开销”策略，页面导航结束后，数据会自动从开发者工具的内存中清除。浏览器会释放响应数据以避免过度占用内存，除非您在页面卸载之前查看这些数据。

由于这是 Chrome 开发者工具的固有行为，因此需要使用以下常见解决方法来检查数据：

- 立即查看请求：在页面导航离开之前，单击“网络”标签页中的请求，以确保响应已加载到开发者工具面板的内存中。
- 使用断点：在“源代码”标签页中，使用“事件监听器断点”部分在 beforeunload 或 unload 事件上设置断点。这将在页面更改之前暂停 JavaScript 执行，从而允许您在“网络”标签页中检查网络请求。
- （推荐采用这个方案）使用外部工具：对于大型响应或持续存在的问题，可以使用 Fiddler 或 Postman 等外部代理工具在浏览器环境之外捕获并查看完整的 HTTP 响应。
- （实践中采用这个方案）您可以在 Chrome 开发者工具中复制请求，方法是右键单击请求并选择“复制”>“复制为 cURL”，然后将其导入到 Postman 等工具中。
