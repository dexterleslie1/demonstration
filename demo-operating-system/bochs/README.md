# 演示bochs使用

## Ubuntu bochs安装

```shell
# 安装bochs-x，否则会报告dlopen failed for module 'x' (libbx_x.so): file not found错误
sudo apt install bochs-x

# 安装bochs
sudo apt install bochs

# 启动bochs，根据提示选择相应的启动选项
bochs
```

## bochs调试

```shell
# 执行build.sh制作disk.img
sh build.sh

# 运行bochs，bochs虚拟机会使用当前目录下的bochsrc配置文件
bochs
```

注意：在学习过程中慢慢学习bochs相关调试命令