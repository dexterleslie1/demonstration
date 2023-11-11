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
