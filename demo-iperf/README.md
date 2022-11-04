# 开源项目iperf研究

## clion和autotools集成原理

通过下面使用clion调试iperf2代码经验，大概了解到clion通过调用autotools相关命令生成configure文件，再调用configure生成makefile，在以后的运行或者调试中通过调用make --makefile=Makefile all先编译代码再使用gdb调试生成的二进制文件。

## 在ubuntu20使用autotools编译iperf2

```shell
apt-get update
apt install make g++ -y

# 不使用gdb调试
./configure

# 使用gdb调试代码
./configure --enable-debug-symbols CFLAGS='-O0 -g3' CXXFLAGS='-O0 -g3'

make -j64
make install
iperf -c 192.168.1.xxx -t 180 -d
```

## 在ubuntu20使用clion调试iperf2代码

使用clion-2022.2.4

- 使用clion选择以makefile类型项目打开
- Build, Execution, Deployment > Makefile > Pre-configuration commands最后添加--enable-debug-symbols CFLAGS='-O0 -g3' CXXFLAGS='-O0 -g3'（一定要设置-O0 -g3，否则无法命中断点并调试代码，-O0关闭gcc代码自动优化，-g3启动gcc调试模式编译代码）
- 配置target all Edit Configurations > Run/Debug Configurations > Executable选择iperf-2.1.8/build/src/iperf可执行文件，program arguments: -c 192.168.1.xxx -t 180 -d（clion这个界面的before launch会自动触发Build任务，使用target all进行编译make --makefile=Makefile all，然后在调用iperf可执行文件进行debug）
- 正常使用clion run和debug功能运行或者调试程序