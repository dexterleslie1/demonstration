# `vsphere`服务器安装

## `vsphere 7.0.3`安装

`vsphere esxi 7.0.3`许可证：`JA0W8-AX216-08E19-A995H-1PHH2`

`vsphere vCenter standard 7.0.3`许可证：`104HH-D4343-07879-MV08k-2D2H2`

下面演示使用`VMware Workstation 17 Pro`安装`vsphere 7.0.3`



### 安装`ESXi7`服务器

选择`VMware-VMvisor-Installer-7.0U3g-20328353.x86_64.iso`镜像安装`ESXi7`服务器，内存为`32G`，`CPU`为`12`核，硬盘容量为`1T`。

根据`installer`的提示选择相应的选项

`ESXi7`服务器安装完毕并重启后，在`ESXi7`服务器主界面中按`F2`进入设置界面，导航到`Configure Management Network`>`IPv4 Configuration`功能，`IPv4`设置信息如下：

- 选择`Set static IPv4 address and network configuration:`
- `IPv4 Address`为`192.168.235.49`
- `Subnet Mask`为`255.255.255.0`
- `Default Gateway`为`192.168.235.2`

按`Enter`按键确认修改信息。

导航到`Configure Management Network`>`IPv6 Configuration`功能，`IPv6`设置信息如下：

- 选择`Disable IPv6(restart required)`

按`Enter`按键确认修改信息。

提示`Apply changes and reboot host?`时，按`Y`按键确认。

访问`https://192.168.235.49/`登录`EXSi7`服务器，帐号：`root`，密码：`在安装ESXi7服务器时指定的密码`

导航到`管理`>`许可`>`分配许可证`功能，设置信息如下：

- 许可证密钥为`JA0W8-AX216-08E19-A995H-1PHH2`

点击`检查许可证`>`分配许可证`按钮完成许可证添加。



### 安装`windows2016`

通过`windows2016`使用`VMware-VCSA-all-7.0.3-20395099.iso`安装`vCenter`服务器，注意：`windows2016`只是作为过渡用途，不会实际运行`vCenter`，所以`windows2016`不需要配置`ip`地址和不需要配置很高的配置，`vCenter`安装程序会自动在`esxi`主机上启动并安装一台独立的虚拟机作为`vCenter-7.0.3`的`guest`系统。需要完成`Stage1`和`Stage2`配置才退出`vCenter`安装程序。

访问`https://192.168.235.49/`登录`EXSi7`服务器，帐号：`root`，密码：`在安装ESXi7服务器时指定的密码`

上传`windows2016`操作系统安装镜像`cn_windows_server_2016_updated_feb_2018_x64_dvd_11636703.iso`到`EXSi7`存储中

使用上面的`windows2016`镜像在`ESXi7`服务器上创建一个虚拟机，虚拟机信息如下：

- 名称为`windows2016-vCenter-initializer`
- 兼容性为`ESXi 7.0 U2 虚拟机`
- 客户机操作系统系列为`Windows`
- 客户机操作系统版本为`Microsoft Windows Server 2016 (64位)`
- 启用`Windows`基于虚拟化的安全性为`勾选`
- 选择存储为`datastore1`
- `CPU`为`4`
- 内存为`8G`
- 硬盘为`100G`，磁盘置备选择`精简置备`
- `CD/DVD`驱动器`1`连接`勾选`，数据存储`ISO`文件为`windows2016 iso`安装镜像文件

按照提示安装`windows2016`操作系统，安装过程关键步骤设置信息如下：

- 选择要安装的操作系统选择`Windows Server 2016 Datacenter (桌面体验)`



### 安装`vCenter`

上传`vCenter`安装镜像`VMware-VCSA-all-7.0.3-20395099.iso`到`ESXi7`存储中

挂载`vCenter`安装镜像到`window2016`的`CD/DVD`驱动器`1`中，在`windows2016`系统中打开`DVD驱动器\vcsa-ui-installer\win32\installer`安装程序，部署新的`vCenter`阶段关键步骤设置信息如下：

- 安装程序首页选择`Install a new vCenter Server`
- `Set up vCenter Server VM`中的`VM name`为`vCenter.51`
- `Select datastore`中`Enable Thin Disk Mode`为`勾选`
- `Configure network settings`设置信息如下：
  - `IP version`为`IPv4`
  - `IP assigment`为`static`
  - `FQDN`不填写
  - `IP address`为`192.168.235.51`
  - `Subnet mask or prefix length`为`255.255.255.0`
  - `Default gateway`为`192.168.235.2`
  - `DNS Servers`为`192.168.235.2,114.114.114.114`
  - `HTTP`为`80`
  - `HTTPS`为`443`

点击`Finish`按钮等待`vCenter`阶段`1`部署完毕。

设置`vCenter`阶段关键步骤信息如下：

- `vCenter Server Configuration`设置信息如下：
  - `Time synchronization mode`选择`Synchronize time with ESXi host`
  - `SSH access`选择`Disabled`
- `SSO Configuration`设置信息如下：
  - 选择`Create a new SSO domain`
  - `Single Sign-On domain name`为`vsphere.local`

点击`Finish`按钮等待`vCenter`阶段`2`设置完毕。

关闭`windows2016-vCenter-initializer`虚拟机

访问`https://192.168.235.51/`登录`vCenter`，帐号：`administrator@vsphere.local`，密码：`安装vCenter时设置的密码`

导航到`系统管理`>`许可`>`许可证`功能，点击`添加`按钮，许可证设置信息如下：

- 输入许可证密钥为`104HH-D4343-07879-MV08k-2D2H2`

点击`添加`按钮完成许可证添加，导航到`系统管理`>`许可`>`许可证`>`资产`功能，点击`分配许可证`按钮，选择刚刚新增的许可证分配给`vCenter`以替换目前使用的评估许可证。



### 添加`ESXi7`服务器到`vCenter`

访问`https://192.168.235.51/`登录`vCenter`，帐号：`administrator@vsphere.local`，密码：`安装vCenter时设置的密码`

导航到`清单`功能，选中`192.168.235.51`（`vCenter`）点击右键弹出上下文菜单，点击菜单中`新建数据中心`功能，数据中心设置信息如下：

- 名称为`Datacenter`
- 位置为`192.168.235.51`

点击`确定`按钮新建数据中心。选中刚刚新建的`Datacenter`数据中心点击右键弹出上下文菜单，点击菜单中`添加主机`功能，主机设置信息如下：

- 主机名或`IP`地址为`192.168.235.49`
- 位置为`Datacenter`
- 用户名为`root`
- 密码为`新建ESXi7服务器的密码`
- 分配许可证选择上面新增的许可证
- 锁定模式选择`禁用`
- 虚拟机位置选择`Datacenter`

点击`完成`按钮完成主机的新增。



## `vCenter`权限管理

通过对不同用户授予不同的对象权限以达到权限控制效果。

访问`https://192.168.235.51/`登录`vCenter`，帐号：`administrator@vsphere.local`，密码：`安装vCenter时设置的密码`



### 新建虚拟机和模板文件夹

新建虚拟机和模板文件夹以分开不同权限的虚拟机或者模板存放在不同的文件夹中，通过统一控制虚拟机和模板文件夹权限以达到统一控制其下子对象（虚拟机和模板）权限的目的。

导航到`清单`功能，选中数据中心`Datacenter`点击右键弹出上下文菜单，在菜单中点击`新建文件夹`>`新建虚拟机和模板文件夹`功能分别创建文件夹`private`和`public`，虚拟机和模板文件夹设置信息如下：

- 输入文件夹名称为`private`、`public`（注意：分别创建文件夹）

点击`确定`按钮创建文件夹。

分别把`windows2016-vCenter-initializer`虚拟机拖拽到`public`文件夹内，把`vCenter.51`虚拟机拖拽到`private`文件夹内。



### 创建用户

导航到`系统管理`>`Single Sign On`>`用户和组`功能，切换`域`到`vsphere.local`，点击`添加`按钮，用户设置信息如下：

- 用户名为`root`

点击`添加`按钮以创建用户。



### 创建角色

创建角色有以下权限：

- 对`public`文件夹的虚拟机和模板有所有权限
- 不能查看`private`文件夹中的虚拟机和模板
- 能够在`public`文件夹中创建虚拟机

导航到`系统管理`>`角色`功能，角色提供程序切换到`VSPHERE.LOCAL`，点击`新建`按钮，角色设置信息如下：

- 角色名称为`default-public`
- 数据存储>分配空间权限为`勾选`
- 虚拟机权限为`全选`
- 主机>创建虚拟机、删除虚拟机、重新配置虚拟机权限为`勾选`
- 资源>将虚拟机分配给资源池权限为`勾选`

点击`创建`按钮以创建角色。



### 用户和对象授权

导航到`系统管理`>`清单`>`数据中心Datacenter`>`权限`功能，点击`添加`按钮，权限设置信息如下：

- 域选择`vsphere.local`
- 用户/组为`root`
- 角色选择`default-public`
- 传播到子对象为`不勾选`

点击`确定`按钮以添加权限。

导航到`系统管理`>`清单`>`数据中心Datacenter`>`public文件夹`>`权限功能，点击`添加`按钮，权限设置信息如下：

- 域选择`vsphere.local`
- 用户/组为`root`
- 角色选择`default-public`
- 传播到子对象为`勾选`

点击`确定`按钮以添加权限。

导航到`系统管理`>`清单`>`数据中心Datacenter`>`主机192.168.235.49`>`权限`功能，点击`添加`按钮，权限设置信息如下：

- 域选择`vsphere.local`
- 用户/组为`root`
- 角色选择`default-public`
- 传播到子对象为`不勾选`

点击`确定`按钮以添加权限。

导航到`系统管理`>`清单`>`数据中心Datacenter`>`datastore1`>`权限`功能，点击`添加`按钮，权限设置信息如下：

- 域选择`vsphere.local`
- 用户/组为`root`
- 角色选择`default-public`
- 传播到子对象为`不勾选`

点击`确定`按钮以添加权限。



### 测试授权是否正常

登录`vCenter`后只能查看到`public`文件夹中的虚拟机和模板，不能够查看`private`文件夹中的虚拟机和模板。

能够正常克隆并运行`windows2016-vCenter-initializer`虚拟机。



## 问题列表

- [Vcenter 7.0 添加主机报错（出现了常规系统错误: Unable to push signed certificate to host 172.17.5.242）](https://blog.csdn.net/JackMaF/article/details/124723108)

  通过`web`登录`vCenter`后选择 主机和集群 > 选中最上面的`vCenter` > 配置 > 设置 > 高级设置 > 点击编辑设置中通过过滤器。搜索到`vpxd.certmgmt.mode`将值从默认的`vmca`更改为`thumbprint`保存。不需要重启从新尝试添加主机即可解决问题。 