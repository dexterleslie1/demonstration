## 许可证

vsphere exsi 7.0.3许可证：JA0W8-AX216-08E19-A995H-1PHH2

vsphere vCenter standard 7.0.3许可证：104HH-D4343-07879-MV08k-2D2H2

## vsphere 7.0.3安装

使用VMware-VMvisor-Installer-7.0U3g-20328353.x86_64.iso安装esxi服务器

通过windows2016使用VMware-VCSA-all-7.0.3-20395099.iso安装vCenter服务器，注意：windows2016只是作为过渡用途，不会实际运行vCenter，所以windows2016不需要配置ip地址和不需要配置很高的配置，vCenter安装程序会自动在esxi主机上启动并安装一台独立的虚拟机作为vCenter-7.0.3的guest os。

## 问题列表

- [Vcenter 7.0 添加主机报错（出现了常规系统错误: Unable to push signed certificate to host 172.17.5.242）](https://blog.csdn.net/JackMaF/article/details/124723108)

  通过web登录vcenter后选择主机和集群>选中最上面的vcenter>配置>设置>高级设置>点击编辑设置中通过过滤器。搜索到vpxd.certmgmt.mode将值从默认的vmca更改为thumbprint保存。不需要重启从新尝试添加主机即可解决问题。 