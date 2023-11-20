## 单元测试

```
# 运行所有测试用例
go test -v

# 运行指定测试用例文件
go test -v add_test.go cal.go

# 运行指定测试用例，NOTE: TestAdd被解析为正则表达式表示会执行TestAdd开头的测试
go test -v -run TestAdd

# 精确地指定运行测试用例
# https://stackoverflow.com/questions/26092155/just-run-single-test-instead-of-the-whole-suite
go test -v -run ^TestAdd$

```

## benchmark测试

> https://stackoverflow.com/questions/16161142/how-to-test-only-one-benchmark-function

```
# 指定运行benchmark测试，不运行unittest
# -bench 指定运行以Benchmark开头的方法
go test -bench Benchmark -run ^$
```



## 单元测试stdin mock

> 单元测试时，读取stdin即时返回EOF，所以单元测试时候无法实现手动输入功能。此情况只能使用mock stdin方式实现模拟手动输入。
>
> 参考 demo-read-stdin_test.go



## os.Stdin调试和运行注意

> 参考使用 demo-unittest-and-benchmarks/demo-read-stdin 调试下面提及到的各种场景。
>
> 使用goland和vscode对涉及os.Stdin需要读取的程序，调试方法是有所区别的。



### goland本地开发场景

```shell
# 从命令行Run程序，能够正常读取stdin
go run main.go

# 从goland Run程序，能够正常读取stdin

# 从golang Debug程序，能够正常读取stdin

# 从命令行Run unittest程序，不能正常读取stdin遇到EOF错误
go test -v

# 从goland Run unittest程序，不能正常读取stdin遇到EOF错误

# 从golang Debug unittest程序，不能正常读取stdin遇到EOF错误
```



### goland远程开发场景

```shell
# 从命令行Run程序，能够正常读取stdin
go run main.go

# 从goland Run程序，不能正常读取stdin，尝试手动输入字符控制台没有反应

# 从golang Debug程序，不能正常读取stdin，尝试手动输入字符控制台没有反应

# 从命令行Run unittest程序，不能正常读取stdin遇到EOF错误
go test -v

# 从goland Run unittest程序，不能正常读取stdin遇到EOF错误

# 从golang Debug unittest程序，不能正常读取stdin遇到EOF错误
```



### vscode本地开发场景

```shell
# 从命令行Run程序，能够正常读取stdin
go run main.go

# 从vscode launch.json Run/Debug程序，能够正常读取stdin，launch.json内容如下:
{
    "version": "0.2.0",
    "configurations": [
        {
            "name": "Launch Package",
            "type": "go",
            "request": "launch",
            "mode": "auto",
            "program": "${workspaceFolder}/main.go",
            "console": "integratedTerminal"
        }
    ]
}

# 从命令行Run unittest程序，不能正常读取stdin遇到EOF错误
go test -v

# 从vscode launch.json Run/Debug unittest程序，能够正常读取stdin，launch.json内容如下:
{
    "version": "0.2.0",
    "configurations": [
        {
            "name": "Launch Package",
            "type": "go",
            "request": "launch",
            "mode": "auto",
            // "program": "${workspaceFolder}/main.go",
            "program": "${workspaceFolder}/demo_test.go",
            "args": [
                "-test.run=^TestReadStdin$"
            ],
            "console": "integratedTerminal"
        }
    ]
}
```



### vscode远程开发场景

```shell
# 从命令行Run程序，能够正常读取stdin
go run main.go

# 从vscode launch.json Run/Debug程序，能够正常读取stdin，launch.json内容如下:
{
    "version": "0.2.0",
    "configurations": [
        {
            "name": "Launch Package",
            "type": "go",
            "request": "launch",
            "mode": "auto",
            "program": "${workspaceFolder}/main.go",
            "console": "integratedTerminal"
        }
    ]
}

# 从命令行Run unittest程序，不能正常读取stdin遇到EOF错误
go test -v

# 从vscode launch.json Run/Debug unittest程序，能够正常读取stdin，launch.json内容如下:
{
    "version": "0.2.0",
    "configurations": [
        {
            "name": "Launch Package",
            "type": "go",
            "request": "launch",
            "mode": "auto",
            // "program": "${workspaceFolder}/main.go",
            "program": "${workspaceFolder}/demo_test.go",
            "args": [
                "-test.run=^TestReadStdin$"
            ],
            "console": "integratedTerminal"
        }
    ]
}
```

