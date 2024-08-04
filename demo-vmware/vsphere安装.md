# `vsphere`服务器安装

## `vsphere 7.0.3`安装

`vsphere exsi 7.0.3`许可证：JA0W8-AX216-08E19-A995H-1PHH2

`vsphere vCenter standard 7.0.3`许可证：104HH-D4343-07879-MV08k-2D2H2

安装步骤：

- 使用`VMware-VMvisor-Installer-7.0U3g-20328353.x86_64.iso`安装`esxi`服务器
- 通过`windows2016`使用`VMware-VCSA-all-7.0.3-20395099.iso`安装`vCenter`服务器，注意：`windows2016`只是作为过渡用途，不会实际运行`vCenter`，所以`windows2016`不需要配置`ip`地址和不需要配置很高的配置，`vCenter`安装程序会自动在`esxi`主机上启动并安装一台独立的虚拟机作为`vCenter-7.0.3`的`guest`系统。需要完成`Stage1`和`Stage2`配置才退出`vCenter`安装程序；在安装`VMware-VCSA-all-7.0.3-20395099.iso`时需要打开`iso`目录并打开`vcsa-ui-installer\win32\installer`安装程序。

问题列表：

- [Vcenter 7.0 添加主机报错（出现了常规系统错误: Unable to push signed certificate to host 172.17.5.242）](https://blog.csdn.net/JackMaF/article/details/124723108)

  通过`web`登录`vCenter`后选择 主机和集群 > 选中最上面的`vCenter` > 配置 > 设置 > 高级设置 > 点击编辑设置中通过过滤器。搜索到`vpxd.certmgmt.mode`将值从默认的`vmca`更改为`thumbprint`保存。不需要重启从新尝试添加主机即可解决问题。 