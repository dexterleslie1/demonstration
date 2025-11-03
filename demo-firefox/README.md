## 安装

### `Windows11`

访问 https://www.firefox.com/ 下载最新版本 `Firefox`并根据提示安装即可（安装过程是在线下载并安装需要耐心等待）。



## `Ubuntu`升级浏览器

升级本地包索引和包列表

```sh
sudo apt update
```

升级 `Firefox` 浏览器

```sh
sudo apt install firefox
```



## 安装`reat-devtools`插件

升级 `Firefox` 到最新版本

访问 https://addons.mozilla.org/en-US/firefox/addon/react-devtools/ 在线安装 `react-devtools` 插件



## 配置代理

打开 `General` > `Network Settings` > `Settings`，选择 `Manual proxy configuration`：

- `SOCKS Host` 填写 `127.0.0.1`
- `Port` 填写 `1080`
- `No proxy for` 填写 `192.168.0.0/16,127.0.0.0/8,::1,www.example.com`
- 勾选 `Proxy DNS when using SOCKS v5`

## 清除缓存

导航到Settings > Privacy & Security功能，找到Cookies and Site Data部分后点击后面的Clear Data按钮即可清除缓存。





