## 删除macOS隔离文件权限

> https://malcontentboffin.com/2019/12/macOS-Error-bin-bash-bad-interpreter-Operation-not-permitted.html

显示文件隔离属性

```sh
xattr -l my-file.sh
```

删除文件隔离属性

```sh
xattr -d com.apple.quarantine my-file.sh
```

递归删除文件和目录的隔离属性

```sh
xattr -r -d com.apple.quarantine Kap.app/
```



## macOS 远程桌面管理方案

NOTE：没有找到相关资料在 macOS 中安装 rdp 服务。没有尝试内置的远程管理功能(ARD)，因为 windows 没有 ARD 客户端软件。

启用内置的屏幕共享和文件共享功能，其中文件共享功能用于解决 realvnc viewer 无法复制粘贴内容时使用。

下载最新版本的 realvnc viewer 连接到服务器。



## `macOS`虚拟机安装

注意：尝试使用`virtualbox`运行`macOS11`和`macOS12`未能够成功，提示“amd-v is not available”错误可能是因为使用`amd cpu`虚拟机化时需要特殊配置，没有做实验证明。



### `vmware workstation17`安装`macOS11`和`macOS12`

注意：`macOS`虚拟机不能启用`CPU虚拟化IOMMU(IO内存管理单元)`特性，否则在启动过程中崩溃。

下载`macOS11`和`macOS12`操作系统 iso，`https://archive.org/details/macos_iso`

上传`macOS11`和`macOS12`操作系统`iso`到`esxi7`存储中

从`https://github.com/DrDonk/unlocker`下载最新版本`unlocker427.zip`并执行其中的`windows/unlock.exe`，注意：执行`unlock.exe`时先关闭运行中的`vmware workstation`。

重启`windows`操作系统后创建虚拟机，注意：固件类型选择`UEFI`，否则无法从`iso`引导安装；操作系统类型选择苹果系统的相应版本。

针对`AMD cpu`需要编辑虚拟机对应的`xxx.vmx`文件并加入下面内容才能够启动`macOS`虚拟机，否则虚拟机一直停顿在苹果`logo`界面

> `AMD CPU`安装`macOS`需要修改`vmx`文件
> https://www.nakivo.com/blog/run-mac-os-on-vmware-esxi/
> https://stackoverflow.com/questions/67025805/vmware-macos-bigsur-in-win10

```properties
cpuid.0.eax = "0000:0000:0000:0000:0000:0000:0000:1011"
cpuid.0.ebx = "0111:0101:0110:1110:0110:0101:0100:0111"
cpuid.0.ecx = "0110:1100:0110:0101:0111:0100:0110:1110"
cpuid.0.edx = "0100:1001:0110:0101:0110:1110:0110:1001"
cpuid.1.eax = "0000:0000:0000:0001:0000:0110:0111:0001"
cpuid.1.ebx = "0000:0010:0000:0001:0000:1000:0000:0000"
cpuid.1.ecx = "1000:0010:1001:1000:0010:0010:0000:0011"
cpuid.1.edx = "0000:0111:1000:1011:1111:1011:1111:1111"
```

`vmware workstation17`破解序列号

> [序列号参考链接](https://gist.github.com/PurpleVibe32/30a802c3c8ec902e1487024cdea26251)

```
MC60H-DWHD5-H80U9-6V85M-8280D
```





### esxi7 update3 安装 macOS11 和 macOS12

参考

> https://vmscrub.com/installing-macos-12-monterey-on-vmware-esxi-7-update-3/

下载 macOS11 和 macOS12 操作系统 iso，https://archive.org/details/macos_iso

上传 macOS11 和 macOS12 操作系统 iso 到 esxi7 存储中

从 https://github.com/erickdimalanta/esxi-unlocker 下载 esxi-unlocker-master.zip

上传 esxi-unlocker-master.zip 到 esxi7 存储后，ssh exsi7 主机解压 zip

```sh
unzip esxi-unlocker-master.zip
```

设置 esxi-unlocker-master 权限

```sh
chmod 775 -R esxi-unlocker-301/
```

检查是否已经安装过 unlocker，如果没有安装过 unlocker 下面命令会输出 smcPresent = false

```sh
./esxi-smctest.sh
```

安装 unlocker

```sh
./esxi-install.sh
```

重启 esxi7 服务器

```sh
reboot
```

重启成功后再次检查 unlocker 是否成功安装，如果成功安装下面命令会输出 smcPresent = true

```sh
./esxi-smctest.sh
```

针对 AMD cpu 需要编辑虚拟机对应的 xxx.vmx 文件并加入下面内容才能够启动 macOS 虚拟机，否则虚拟机一直停顿在苹果logo界面

> AMD CPU 安装 macOS 需要修改 vmx 文件
> https://www.nakivo.com/blog/run-mac-os-on-vmware-esxi/
> https://stackoverflow.com/questions/67025805/vmware-macos-bigsur-in-win10

```properties
cpuid.0.eax = "0000:0000:0000:0000:0000:0000:0000:1011"
cpuid.0.ebx = "0111:0101:0110:1110:0110:0101:0100:0111"
cpuid.0.ecx = "0110:1100:0110:0101:0111:0100:0110:1110"
cpuid.0.edx = "0100:1001:0110:0101:0110:1110:0110:1001"
cpuid.1.eax = "0000:0000:0000:0001:0000:0110:0111:0001"
cpuid.1.ebx = "0000:0010:0000:0001:0000:1000:0000:0000"
cpuid.1.ecx = "1000:0010:1001:1000:0010:0010:0000:0011"
cpuid.1.edx = "0000:0111:1000:1011:1111:1011:1111:1111"
```

