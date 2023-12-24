## ginkgo参考资料

> https://github.com/onsi/ginkgo
> https://onsi.github.io/ginkgo/



## 设置和运行ginkgo测试套件

创建`go mod`管理的项目

```shell
sudo go mod init demo-lib-ginkgo
```

安装`ginkgo`命令

```shell
sudo go get github.com/onsi/ginkgo/v2
sudo go install github.com/onsi/ginkgo/v2/ginkgo
```

前者命令`sudo go get ...`在`go.mod`添加依赖，后者命令`sudo go install ...`下载并安装`ginkgo`命令到`$PATH`中

验证`ginkgo`命令是否正确安装

```shell
ginkgo version
```

命令结果会输出`ginkgo`版本

添加`ginkgo`测试套件

```shell
cd demo-lib-ginkgo
ginko bootstrap
```

`ginkgo bootstrap`命令会在当前目录生成名为`demo_lib_ginkgo_suite_test.go`文件

下载`ginkgo`相关依赖

```shell
go mod tidy
```

使用命令`go test -v`运行测试，输出结果如下：

```shell
=== RUN   TestDemoLibGinkgo
Running Suite: DemoLibGinkgo Suite - /home/xxx/workspace-git/demonstration/demo-golang/demo-lib-ginkgo
===============================================================================================================
Random Seed: 1703426515

Will run 0 of 0 specs

Ran 0 of 0 Specs in 0.000 seconds
SUCCESS! -- 0 Passed | 0 Failed | 0 Pending | 0 Skipped
--- PASS: TestDemoLibGinkgo (0.00s)
PASS
ok      demo-lib-ginkgo 0.011s
```

或者使用命令`ginkgo`运行测试，输出结果如下：

```shell
Running Suite: DemoLibGinkgo Suite - /home/xxx/workspace-git/demonstration/demo-golang/demo-lib-ginkgo
===============================================================================================================
Random Seed: 1703425776

Will run 0 of 0 specs

Ran 0 of 0 Specs in 0.000 seconds
SUCCESS! -- 0 Passed | 0 Failed | 0 Pending | 0 Skipped
PASS

Ginkgo ran 1 suite in 701.539459ms
Test Suite Passed
```



## 添加一个测试用例到套件中

todo

