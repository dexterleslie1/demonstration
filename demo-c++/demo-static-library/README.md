# 演示静态库的制作和使用

## 说明

demo-project-library 用于生成静态库

demo-project-reference 用于调用静态库测试



## demo步骤

使用项目demo-project-library运行 build.sh 生成静态库

复制静态库头文件mylib.h和libmylib.a到项目demo-project-reference的include和lib目录中（第三方也是这样提供xxx.a和头文件xxx.h供调用方使用）

使用demo-project-reference调用mylib静态库，运行 build.sh 生成 test 可执行文件，./test运行程序
