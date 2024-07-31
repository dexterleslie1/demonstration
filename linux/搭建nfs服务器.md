# 搭建`nfs`服务器

## `centOS7`、`centOS8`搭建`nfs`服务器

>http://blog.huatai.me/2014/10/14/CentOS-7-NFS-Server-and-Client-Setup/
>https://www.howtoforge.com/tutorial/setting-up-an-nfs-server-and-client-on-centos-7/

- 关闭`selinux`、`firewalld`

- 安装`nfs-server`

  ```bash
  yum install nfs-utils -y
  systemctl start nfs-server
  systemctl enable nfs-server
  ```

- 配置`nfs`共享`/data`文件夹，编辑`/etc/exports`文件内容如下：

  ```bash
  /data *(rw,sync,no_root_squash,no_subtree_check)
  ```

- 使`nfs-server export`配置立即生效

  ```bash
  exportfs -a
  ```

- 显示`/data`文件夹是否被`nfs export`

  ```bash
  showmount -e
  ```

- 客户端`mount nfs-server`测试，挂载`nfs-server /data`文件夹到本地`/opt/temp`文件夹

  ```bash
  mkdir /opt/temp
  mount -t nfs 192.168.235.191:/data /opt/temp
  ```

- 客户端主机执行命令显示所有`mount`文件系统

  ```bash
  mount
  ```

- 取消`nfs`挂载

  ```bash
  umount /opt/temp
  ```

  