# `vmware tools`使用

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