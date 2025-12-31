## 截图工具

### 使用微信自带的

使用快捷键Alt+a快捷键调出。

### ~~snipaste~~

>`https://www.snipaste.com`

支持`windows`、`macOS`、`ubuntu`，注意：从`dmg`安装成功后需要手动删除安全隔离属性才能够打开软件，否则报告没有权限打开该软件错误。

截图快捷键为`f1`功能键



#### `Windows 11`安装

访问 https://www.snipaste.com/download.html 下载 `Windows zip` 压缩包，解压压缩包后运行 `exe` 即可运行 `Snipaste`。

运行 `Snipaste` 后设置开机自动启动。



#### `ubuntu20.04`安装

下载`Linux AppImage`：https://www.snipaste.com/download.html

移动`Snipaste-2.10.2-x86_64.AppImage`到`/usr/local`目录并授予执行权限

```bash
sudo mv Snipaste-2.10.2-x86_64.AppImage /usr/local/ && sudo chmod +x /usr/local/Snipaste-2.10.2-x86_64.AppImage
```

运行软件

```bash
cd /usr/local && ./Snipaste-2.10.2-x86_64.AppImage
```

- 如果报告 `./Snipaste-2.10.5-x86_64.AppImage: error while loading shared libraries: libOpenGL.so.0: cannot open shared object file: No such file or directory` 错误，则运行以下命令安装 `libOpenGL`

  >`https://stackoverflow.com/questions/65751536/importerror-libopengl-so-0-cannot-open-shared-object-file-no-such-file-or-dir`

  ```
  sudo apt install libopengl0
  ```

按`f1`截图

通过`Show Applications`>`Startup Application`功能设置`snipaste`开机自启动，开机自启动配置信息如下：

- `Name`填写`snipaste`
- `Command`填写`/usr/local/Snipaste-2.10.2-x86_64.AppImage`
- `Comment`不填写

点击`Add`按钮保存开机自启动。

## 颜色拾取器

~~网站：https://imagecolorpicker.com/，把图片上传到网站中并使用颜色拾取功能吸取颜色。~~

使用微信自带的颜色拾取器，使用快捷键Alt+a调出并使用Ctrl+C复制当前拾取的颜色值。
