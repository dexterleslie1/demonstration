## ansi escape code


### 参考资料

> https://www.lihaoyi.com/post/BuildyourownCommandLinewithANSIescapecodes.html
> https://en.wikipedia.org/wiki/ANSI_escape_code
> https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797



### 调试

> https://jakewharton.com/peeking-at-colorful-command-line-output/

```
# 使用 sed 命令转换特殊的 \x1b[ 控制指令
# NOTE: 此方法只是提供参考，使用asciinema
ls --color | sed -r 's/\x1b\[/\\e\[/g'

### 使用 asciinema 工具

# centOS8 安装 asciinema
yum install asciinema

# 录制命令所有 ANSI escape 输出
asciinema rec -c "ls --color" lsoutput.log


```
