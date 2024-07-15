# `LVM`逻辑卷管理

## 使用`lvm2`管理新的硬盘

>[CentOS 6.3下配置LVM（逻辑卷管理）](https://www.cnblogs.com/mchina/p/linux-centos-logical-volume-manager-lvm.html)

安装`lvm2`

```bash
yum install lvm2 -y
```

新增容量为`20g`的硬盘用于以下测试用途

查看物理硬盘符号

```bash
fdisk -l
```

创建`physical volume`

>注意：硬盘`/dev/sdxxx`不需要使用`fdisk`格式化就可以在其上创建`pv`

```bash
# 在硬盘/dev/sdb /dev/sdc上创建pv
pvcreate /dev/sdb /dev/sdc

# 显示系统pv列表
pvdisplay
pvs
```

创建`volume group`

```bash
# 其中/dev/sdb、/dev/sdc是上面步骤创建的pv名称
vgcreate vg0 /dev/sdb /dev/sdc

# 显示系统vg列表
vgdisplay
vgs
```

创建`10g`的`logical volume`

```bash
# 使用vg0创建名为lv1的lv，容量为10g
lvcreate -L 10g -n lv1 vg0
```

创建占用`vg 100%`可用空间的`lv`卷

>[创建`lv`卷占用`vg`的所有空间](https://www.linuxquestions.org/questions/linux-hardware-18/lvcreate-with-max-size-available-749253/)

```bash
# 使用vg0创建名为lv1的lv，容量为vg0的100%
lvcreate -l 100%FREE -n lv1 vg0
```

显示逻辑卷

```bash
lvdisplay
lvs
```

格式化并且挂载`lv1`

```bash
# 逻辑卷/dev/vg0/lv1创建xfs格式的文件系统
mkfs.xfs /dev/vg0/lv1

# 编辑/etc/fstab添加如下内容开机自动挂载lv到/data目录
/dev/vg0/lv1                            /data   xfs     defaults        0 0
```

手动挂载逻辑卷`/dev/vg0/lv1`到`/data`目录

```bash
mount /dev/vg0/lv1 /data
```

`Logical volume`扩容

```bash
# 逻辑卷/dev/vg0/lv1扩容1g
# -L选项用于直接指定逻辑卷扩展后的总大小（Total Size）
lvextend -L +1g /dev/vg0/lv1

# 逻辑卷/dev/vg0/lv1扩容到12g
# -L选项用于直接指定逻辑卷扩展后的总大小（Total Size）
lvextend -L 12g /dev/vg0/lv1

# -l选项用于指定逻辑卷扩展时要增加的逻辑扩展数（Logical Extents，简称LE）。逻辑扩展是LVM中用于分配存储空间的基本单位，其大小在卷组创建时确定，通常为4MB（但这不是固定值，可以根据需要调整）。如果你想要向逻辑卷/dev/vg1000/lvol0增加100个逻辑扩展，且每个逻辑扩展的大小为4MB（这取决于卷组的配置），则可以使用命令lvextend -l +100 /dev/vg1000/lvol0。注意这里的+表示增加，如果不加+，则表示设置逻辑卷的总LE数为指定值。
# -l +100%FREE表示把逻辑卷所属的vg剩余空间全部用于扩容
lvextend -l +100%FREE /dev/vg0/lv1

# 专门用于扩展或缩小ext2、ext3和ext4文件系统的大小
resize2fs /dev/vg0/lv1

# xfs文件系统使用以下命令使文件resize
xfs_growfs /dev/vg0/lv1
```

添加`pv`到`vg`中，以达到扩容效果

```bash
# 其中/dev/sdd1是physical volume名称
vgextend vg0 /dev/sdd1
```



## `lvm2`其他管理命令

删除`lv`

```bash
# 显示lv列表
lvdisplay

# 删除指定的LV Path的lv，例如：/dev/vg0/lv1
lvremove /dev/vg0/lv1
```

