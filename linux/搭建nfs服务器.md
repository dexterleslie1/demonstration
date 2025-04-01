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

  >`nfs`挂载参数`https://help.aliyun.com/zh/nas/user-guide/mount-an-nfs-file-system-on-a-linux-ecs-instance?spm=a2c4g.11186623.help-menu-27516.d_2_0_2_1_0.4c1cda29aX7laN`

  ```bash
  mkdir /opt/temp
  mount -t nfs -o vers=3,nolock,proto=tcp,rsize=1048576,wsize=1048576,hard,timeo=600,retrans=2 192.168.235.191:/data /opt/temp
  ```

  - `vers=3`：使用`NFS v3`协议挂载文件系统。

  - `rsize`：定义数据块的大小，用于客户端与文件系统之间读取数据。建议值：`1048576`。

  - `wsize`：定义数据块的大小，用于客户端与文件系统之间写入数据。建议值：`1048576`。

    >如果您需要更改`IO`大小参数（`rsize`和`wsize`），建议您尽可能使用最大值（`1048576`），以避免性能下降。

  - `hard`：在文件存储`NAS`暂时不可用的情况下，使用文件系统上某个文件的本地应用程序时会停止并等待至该文件系统恢复在线状态。建议启用该参数。

  - `timeo`：指定时长，单位为`0.1`秒，即`NFS`客户端在重试向文件系统发送请求之前等待响应的时间。建议值：`600`（`60`秒）。

    >如果您必须更改超时参数（`timeo`），建议您使用`150`或更大的值。该`timeo`参数的单位为`0.1`秒，因此`150`表示的时间为`15`秒。

  - `retrans`：`NFS`客户端重试请求的次数。建议值：`2`。

  - `noresvport`：在网络重连时使用新的`TCP`端口，保障在网络发生故障恢复时不会中断连接。建议启用该参数。

- 客户端主机执行命令显示所有`mount`文件系统

  ```bash
  mount
  ```

- 取消`nfs`挂载

  ```bash
  umount /opt/temp
  ```

  