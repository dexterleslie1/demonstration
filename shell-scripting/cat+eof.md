# 演示cat和EOF用法

## 参考资料

> [cat和EOF的配合使用](https://www.cnblogs.com/shanghai1918/p/12930430.html)

## 输出多行文本到testing.txt
```
cat > testing.txt << EOF
{{ work01_hostname }} {{ work01_ip }}
{{ work01_hostname }} {{ work01_ip }}
EOF
```