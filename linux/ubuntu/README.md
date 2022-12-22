## ubuntu20安装googlepinyin输入法

[ubuntu20.04中文输入法安装步骤](https://www.qetool.com/scripts/view/20653.html)

1. 打开language support后点击install安装所有支持语言

2. 打开language support选择输入法方式为fcitx

3. 安装googlepinyin输入法

   ```shell
   sudo apt install fcitx-googlepinyin
   ```

4. 重启系统后点击右上角输入法configure，如果googlepinyin输入法不存在则添加，并且设置切换输入法快捷键为ctrl+alt+shift



## ubuntu20安装visual code

> 不能使用snap安装visual code，因为snap visual code 是阉割版，不能切换中文输入法。

```shell
# 参考文档
# https://code.visualstudio.com/docs/setup/linux

# 下载visual code deb安装包到本地

# 从deb安装包安装visual code
sudo apt install ./code_xxxxx.deb
```

