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

- `SOCKS Host` 填写 `127.0.0.1``
- ``Port` 填写 `1080`
- `No proxy for` 填写 `192.168.0.0/16,127.0.0.0/8,::1,www.example.com`
- 勾选 `Proxy DNS when using SOCKS v5`



