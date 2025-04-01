# `vmware tools`使用

注意：使用`vmware workstation 17.5.0`安装`windows10`并完全安装`vmware tools`不会出现黑屏问题。



## 安装`vmware tools`

### `ubuntu`安装`vmware tools`

1. 点击`vmware workstation`虚拟机 > 安装`VMware Tools`功能后会自动挂载`vmware tools iso`到`/media/dexterleslie/VMware\ Tools`目录中

2. 复制`/media/dexterleslie/VMware\ Tools/VMwareTools-10.3.10-12406962.tar.gz`到`/tmp/`目录

   ```bash
   cp /media/dexterleslie/VMware\ Tools/VMwareTools-10.3.10-12406962.tar.gz /tmp/
   ```

3. 解压`VMwareTools-10.3.10-12406962.tar.gz`

   ```bash
   cd /tmp/ && tar -xvzf VMwareTools-10.3.10-12406962.tar.gz
   ```

4. 根据提示安装`vmware tools`

   ```bash
   cd /tmp/vmware-tools-distrib/ && sudo ./vmware-install.pl
   ```

5. 重启系统



### `macOS`安装`vmware tools`

注意：`macOS 13.0.1`安装`vmware tools`时需要打开`System Settings`>`Privacy & Security`解除`vmware tools`阻拦，否则`vmware tools`会提示安装失败。