## chromium安装SwitchyOmega插件

```
下载SwitchyOmega插件
https://github.com/FelisCatus/SwitchyOmega/releases

直接安装crx会在安装过程出现CRX_HEADER_INVALID错误
https://blog.csdn.net/u014061630/article/details/89715656
参考以上链接方案二：
把crx修改为扩展名zip，拖动zip到chrome地址栏chrome://extensions/中直接安装。如果拖动zip不能自动安装插件则在解压插件zip包，然后在插件管理界面打开开发者模式后点击 "Load Unpacked" 按钮选择解压后的插件目录。

NOTE: 配置完成SwitchyOmega后点击“应用选项”保存设置才能够生效
```



## ubuntu安装google chrome浏览器

> NOTE: 使用sudo snap install chromium安装的浏览器在使用过程中有莫名奇妙的证书域名错误，所以放弃使用chromium浏览器。

```
# 到google chrome官方网站下载deb包
https://www.google.com/chrome

# 使用dpkg安装google chrome
sudo dpkg -i ~/Downloads/google-chrome-stable_current_amd64.deb 

```

