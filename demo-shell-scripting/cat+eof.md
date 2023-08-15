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
```