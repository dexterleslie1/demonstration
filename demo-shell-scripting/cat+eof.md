# 演示cat和EOF用法

## 参考资料

> [cat和EOF的配合使用](https://www.cnblogs.com/shanghai1918/p/12930430.html)
> https://blog.csdn.net/xiaokanfuchen86/article/details/116144694

## 输出多行文本到testing.txt（NOTE：在脚本xxx.sh中最后EOF标识符前面不能有空格，一定要紧贴行首，否则报错）
```
cat > testing.txt << EOF
{{ work01_hostname }} {{ work01_ip }}
{{ work01_hostname }} {{ work01_ip }}
EOF

# <<-EOF用法，shell脚本如下
# https://unix.stackexchange.com/questions/583782/what-is-different-between-eof-and-eof-in-bash-script
# NOTE: EOF之间的内容需要使用tab创建indent，否则<<-EOF不起作用
# https://unix.stackexchange.com/questions/76481/cant-indent-heredoc-to-match-code-blocks-indentation

#!/bin/bash

if [[ "" == "" ]]; then
	cat > testing.txt <<-EOF
		{{ work01_hostname }} {{ work01_ip }}
		{{ work01_hostname }} {{ work01_ip }}
EOF
fi
	
```