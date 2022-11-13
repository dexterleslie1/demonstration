# 演示动态链接库制作和使用

## 说明

 动态链接库编译时需要添加-fPIC，为了编译出so库地址无关代码，编译可执行程序时gcc使用 -l 指定so库（去除lib前缀和.so后缀，例如：libmylib.so为mylib） -L 指定so库路径，发布so库到系统/lib/目录，否则在运行可执行文件时报告无法找到动态链接库错误。



## 运行demo步骤

demo-library运行 sh build.sh

demo-executable运行 sh build.sh

运行 ./test