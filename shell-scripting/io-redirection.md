# 输入、输出重定向

## 参考资料 

> [I/O Redirection](http://tldp.org/LDP/abs/html/io-redirection.html)  
[输入/输出重定向](https://www.runoob.com/linux/linux-shell-io-redirections.html)

## 输出重定向

### 日期输出到文件file1中，file1中内容会被覆盖
```
date > file1
```

### 日期输出到文件file1中，追加内容到文件file1中
```
date >> file1
```

## 输入重定向

### 从文件file1中读取内容并且输出到控制台
```
date > file1
cat < file1
```

## 输入、输出重定向

### 从文件file1中读取内容（输入重定向）后输出到文件file2中（输出重定向）
```
date > file1
cat < file1 > file2
```

## 标准输入（stdin）、输出（stdout）、错误（stderr）

> 一般情况下，每个 Unix/Linux 命令运行时都会打开三个文件：
>- 标准输入文件(stdin)：stdin的文件描述符为0，Unix程序默认从stdin读取数据。
>- 标准输出文件(stdout)：stdout 的文件描述符为1，Unix程序默认向stdout输出数据。
>- 标准错误文件(stderr)：stderr的文件描述符为2，Unix程序会向stderr流中写入错误信息。

### stderr重定向到file1
```
command1 2> file1
```

### 将stdout和stderr合并后重定向到file1，命令行没有内容输出，其中&符号代表标准输出合并的意思
```
date > file1 2>&1
date1 > file1 2>&1
```

### stdout和stderr都不输出
```
date > /dev/null 2>&1
date1 > /dev/null 2>&1
```

### Permission denided错误（stderr）不输出，stdout正常输出到控制台
```
find / -iname "*takeRight*" 2>/dev/null
```
