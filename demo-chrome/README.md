## `chromium`安装`SwitchyOmega`插件

- 下载`SwitchyOmega`插件，`https://github.com/FelisCatus/SwitchyOmega/releases`

- 安装插件

  直接安装`crx`会在安装过程出现`CRX_HEADER_INVALID`错误，参考链接`https://blog.csdn.net/u014061630/article/details/89715656`

  参考以上链接方案二：把`crx`修改为扩展名`zip`，拖动`zip`到`chrome`地址栏`chrome://extensions/`中直接安装。如果拖动`zip`不能自动安装插件则在解压插件`zip`包，然后在插件管理界面打开开发者模式后点击`Load Unpacked`按钮选择解压后的插件目录。

- 配置完成`SwitchyOmega`后点击“应用选项”保存设置才能够生效



## `Ubuntu`安装`google chrome`浏览器

> 注意：使用`sudo snap install chromium`安装的浏览器在使用过程中有莫名奇妙的证书域名错误，所以放弃使用`chromium`浏览器。

- 到`google chrome`官方网站下载`deb`包，在国外访问 https://www.google.com/chrome，在国内访问 https://www.google.cn/intl/zh-CN_ALL/chrome/fallback/

- 使用`dpkg`安装`google chrome`

  ```bash
  sudo dpkg -i ~/Downloads/google-chrome-stable_current_amd64.deb
  ```



## `Windows11`安装`google chrome`浏览器

访问 https://www.google.cn/intl/zh-CN_ALL/chrome/fallback/ 下载安装程序并根据提示安装即可。



## 借助`chrome`判断网站内容是否为`javascript`动态生成

原理：通过启用和禁用`javascript`运行判断网站内容是否为动态生成的。

- 打开`chrome`开发者调试工具
- 使用组合键`ctrl+shift+p`调出命令窗口，输入命令`disable javascript`后按`enter`键用于禁用`javascript`，输入命令`enable javascript`后按`enter`键用于启用`javascript`
- 通过禁用和启用`javascript`切换观察网站页面是否隐藏和显示部分内容判断网站内容是否为动态生成的



## 删除浏览数据

>提示：删除历史记录、`Cookie`、缓存内容及其他数据。

打开`设置` > `隐私与安全` > `删除浏览数据`功能，根据提示删除相应的数据即可。
