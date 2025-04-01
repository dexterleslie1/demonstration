## windows10+ubuntu双系统启动

https://itsfoss.com/install-ubuntu-1404-dual-boot-mode-windows-8-81-uefi/

```
NOTE：只能是windows10，测试windows server2016安装后ubuntu安装程序不能识别windows server2016安装分区

1、安装windows10
2、使用windows磁盘管理工具分割出安装ubuntu的安装分区
3、安装ubuntu，注意选择：Install Ubuntu alongside Windows Boot Manager，完成安装后重新启动就能够通过grub选择启动不同的系统
```
