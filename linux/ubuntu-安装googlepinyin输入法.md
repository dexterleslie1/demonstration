# ubuntu20安装googlepinyin输入法

> ubuntu20.04中文输入法安装步骤参考 [链接](https://www.qetool.com/scripts/view/20653.html)

1. 安装中文语言支持

   ```bash
   sudo apt install -y gnome-user-docs-zh-hans firefox-locale-zh-hans language-pack-zh-hans thunderbird-locale-zh-cn thunderbird-locale-zh-hans language-pack-gnome-zh-hans
   ```

2. 安装google拼音输入法

   ```bash
   sudo apt install -y fcitx-googlepinyin
   ```

3. 打开`language support`选择输入法方式为`fcitx`

4. 重启系统后点击右上角输入法`configure`或者在应用搜索中输入`fcitx configuration`，如果`googlepinyin`输入法不存在则添加，并且设置切换输入法快捷键为`ctrl + alt(macOS为option) + shift`

5. 把`googlepinyin`输入法修改为繁体输入，参考 [链接](https://askubuntu.com/questions/1336435/how-do-i-get-traditional-chinese-input-with-pinyin-on-ubuntu-20-04)

6. 使用`ctrl + shift + f`切换到繁体输入(切换之后永久改变)

