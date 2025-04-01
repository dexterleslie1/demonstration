# `I/O`压力测试

## 概念和相关资料

### 什么是顺序和随机读写？

> 说明什么是顺序和随机读写。[链接](https://blog.csdn.net/markzy/article/details/135869536)
>
> [随机读写](https://en.wikipedia.org/wiki/Random_access)
>
> [顺序读写](https://en.wikipedia.org/wiki/Sequential_access)

顺序读写是指，要读写的[数据存储](https://so.csdn.net/so/search?q=数据存储&spm=1001.2101.3001.7020)在磁盘的一整块连续的地址上。举个例子老师发卷子的例子，老师按座位发卷子，找到坐在第一个的同学，一路走下去就能正确的把卷子发到每位同学的手里。简单来说就是只需要找一次，就可以完成读写数据。

磁盘的随机读写是指，要读写的数据分布在磁盘中多个不连续的地址上。还是老师在班里发试卷，老师按学号发卷子，1号2号3号4号一个个发过去。但是学生的座位并不是根据学号来安排的，所以老师就需要先跑到1号的座位发给他试卷，再去找2号发试卷，以此类推。简单的说就是边找数据边读写。



### 随机读写耗时原因

> 解释为何随机读写耗时的原因。[链接](https://blog.csdn.net/markzy/article/details/135869536)

随机读写和顺序读写的差异在于定位数据的次数。**顺序读写**只需做一次数据定位的操作，剩下就是数据传输的时间。**随机读写**由于数据的物理位置分散所以需要不断的进行重新定位，定位操作会消耗大量的时间。

机械硬盘随机读写过程：硬盘读写耗费的时间可以分为：寻道时间、旋转延迟、数据传输。寻道时间：硬盘收到指令调动磁头摆臂，将摆臂移动到指定磁道的时间。旋转延迟：主轴旋转，将数据所在的扇区移动到磁头下方所花费的时间。数据传输：磁头读取数据传输到主控的时间。其中寻道时间和旋转延迟主要依赖机械移动（寻道时间是机械臂移动而旋转延迟是主轴转动），机械移动的耗时远大于数据传输，是机械硬盘读写慢的主要原因。目前磁盘的平均寻道时间一般在315ms，一般都在10ms左右，磁盘旋转一圈大约需要811ms ，由此得出定位一次数据就需要10~25ms。

固态硬盘随机读写过程：固态硬盘在内部维护了Mapping Table用来存储逻辑地址和物理地址之间的映射关系，确定了物理地址后可以直接对硬盘上的Page进行读写。固态硬盘虽不像机械硬盘一样每次定位数据都需进行机械运动，但访问Mapping Table也会中断读写操作，相比持续进行数据传输的顺序读写还是会慢不少。



### 是否可以通过工具判断当前运行软件是顺序读写还是随机读写呢？

> 解释 [链接](https://unix.stackexchange.com/questions/279963/is-there-a-way-to-monitor-disk-i-o-patterns-i-e-random-or-sequential-i-o)

目前没有现成的工具可以做到这个需求，但是参考上面的链接似乎能够通过编写自己的 FUSE 文件系统监控软件的 I/O行为判断是顺序还是随机读写。



### 什么是同步和异步 I/O 呢？

> 解释 [链接](https://www.baeldung.com/linux/sync-vs-async-mount-options)

sync 是 synchronously 的缩写，意味着对驱动器的 I/O 操作将在文件系统上发生时准确发生，不会导致数据丢失。 async 是 asynchronously 的缩写，意味着对驱动器的 I/O 操作将在内存/缓冲区中保留一段时间，而不是立即写入磁盘，可能会导致数据丢失。



### 在`/etc/fstab`中配置同步或者异步`I/O`

> fstab维基百科 [链接](https://en.wikipedia.org/wiki/Fstab)
>
> /etc/fstab 配置 defaults 默认使用异步（async） I/O 模式，NOTE：一般操作系统挂载卷默认使用 async 模式。[链接](https://en.wikipedia.org/wiki/Fstab)

通过命令 mount -v 查看被挂载卷的 I/O 模式，如果不显示`async`或`sync`表示卷使用默认`async`模式，如果显示 sync 则表示卷使用 sync 模式。

```sh
mount -v
```



### Google PD 性能指标对照表

> 对照表 [链接](https://cloud.google.com/compute/docs/disks/performance)
>
> 在`GCP`中使用`E2 VMs e2-medium`测试证明`GCP`提供的`I/O`性能承诺是符合实际的。



## 使用`fio`作为基准工具测试`I/O`性能

> 需要适当调节`--bs I/O` 单元的块大小才能够跑满硬盘`I/O`吞吐量。
>
> `fio`使用参考文档 [链接](https://fio.readthedocs.io/en/latest/)

`fio`命令行参数：

- --name 当前运行 job 的名称
- --directory 存放测试过程中生成的测试文件目录
- --numjobs 创建此作业的指定数量的克隆。 每个作业克隆都作为独立的线程或进程生成。 可用于设置大量线程/进程来执行相同的操作。 每个线程单独报告； 要查看所有克隆的整体统计信息，请将 group_reporting 与 new_group 结合使用。 请参阅 --max-jobs。 默认值：1。
- --size 该作业每个线程的文件 I/O 总大小。 Fio 将运行直到传输了这么多字节，除非运行时通过其他方式更改，例如 (1) 运行时、(2) io_size (3) number_ios、(4) 在执行 I/O 时的间隙/孔（例如 rw=read） :16K，或 (5) 顺序 I/O 到达文件末尾，当 Percentage_random 小于 100 时，这是可能的。Fio 将在由 nrfiles、文件名等选项确定的可用文件之间划分此大小，除非文件大小由 工作。 如果除法结果恰好为 0，则大小将设置为给定文件或设备（如果存在）的物理大小。 如果未指定此选项，fio 将使用给定文件或设备的完整大小。 如果文件不存在，则必须给出大小。 也可以将大小指定为 1 到 100 之间的百分比。如果指定 size=20%，fio 将使用给定文件或设备的完整大小的 20%。 在 ZBD 模式下，还可以使用“z”将值设置为区域数。 可以与 offset 结合使用来限制 I/O 将在其中完成的开始和结束范围。
- --runtime 限制运行时间。 测试将一直运行，直到完成配置的 I/O 工作负载或运行指定的时间（以先发生者为准）。 确定指定作业将运行多长时间可能非常困难，因此此参数可以方便地将总运行时间限制在给定时间。 当省略单位时，该值以秒为单位解释
- --time_based 如果设置，即使文件已完全读取或写入，fio 也将在指定的运行时时间内运行。 它将简单地在运行时允许的情况下循环相同的工作负载多次。
- --ramp_time 如果设置，fio 将在记录任何性能数据之前运行指定的工作负载一段时间。 对于在记录结果之前让性能稳定下来非常有用，从而最大限度地减少稳定结果所需的运行时间。 请注意，ramp_time 被认为是作业的提前时间，因此如果指定特殊超时或运行时间，它将增加总运行时间。 当省略单位时，该值以秒为单位给出。
- --ioengine 定义作业如何向文件发出 I/O。默认值：libaio
- --direct 如果值为 true，则使用非缓冲 I/O。 这通常是 O_DIRECT。 请注意，Solaris 上的 OpenBSD 和 ZFS 不支持直接 I/O。 在 Windows 上，同步 ioengine 不支持直接 I/O。 默认值：假
- --verify 如果写入文件，fio 可以在作业的每次迭代后验证文件内容。 每种验证方法还意味着对特殊标头的验证，该标头被写入每个块的开头。 该标头还包括元信息，例如块的偏移量、块编号、写入块时的时间戳等。verify 可以与 verify_pattern 选项结合使用。
- --bs 用于 I/O 单元的块大小（以字节为单位）。 默认值：4096。
- --iodepth 针对文件保持运行的 I/O 单元数。 请注意，将 iodepth 增加到超过 1 不会影响同步 ioengine（使用 verify_async 时的小程度除外）。 即使异步引擎也可能会施加操作系统限制，导致无法达到所需的深度。 在 Linux 上使用 libaio 且未设置 direct=1 时可能会发生这种情况，因为缓冲 I/O 在该操作系统上不是异步的。 密切关注 fio 输出中的 I/O 深度分布，以验证所实现的深度是否符合预期。 默认值：1。
- --rw I/O 模式的类型。https://fio.readthedocs.io/en/latest/fio_doc.html#cmdoption-arg-readwrite
- --group_reporting 有时，显示整个作业组的统计数据而不是每个单独作业的统计数据可能会很有趣。 如果使用 numjobs，则尤其如此； 查看单个线程/进程的输出很快就会变得难以处理。 要查看每个组而不是每个作业的最终报告，请使用 group_reporting。 文件中的作业将属于同一报告组，除非被石墙分隔或使用 new_group。
- --iodepth_batch_submit 这定义了一次提交多少个 I/O。 它默认为 1，这意味着我们会在每个 I/O 可用时立即提交它，但可以提高以在当时提交更大批量的 I/O。 如果设置为 0，将使用 iodepth 值。
- --iodepth_batch_complete_max 这定义了一次检索的最大 I/O 块数。



使用多个并行流 (16+) 执行顺序写入、使用 1 MB 的 I/O 块大小和至少 64 的 I/O 深度来测试写入吞吐量

```sh
sudo fio --name=test_iops --directory=/home/temp --numjobs=8 \
--size=10G --time_based --runtime=60s --ramp_time=2s --ioengine=libaio \
--direct=1 --verify=0 --bs=1M --iodepth=64 --rw=write \
--group_reporting=1 --iodepth_batch_submit=64 \
--iodepth_batch_complete_max=64
```

使用多个并行流 (16+) 执行顺序读取、使用 1 MB 的 I/O 块大小和至少 64 的 I/O 深度来测试读取吞吐量

```sh
sudo fio --name=test_iops --directory=/home/temp --numjobs=8 \
--size=10G --time_based --runtime=60s --ramp_time=2s --ioengine=libaio \
--direct=1 --verify=0 --bs=1M --iodepth=64 --rw=read \
--group_reporting=1 \
--iodepth_batch_submit=64 --iodepth_batch_complete_max=64
```

使用多个并行流 (16+) 执行顺序读写、使用 1 MB 的 I/O 块大小和至少 64 的 I/O 深度来测试读取吞吐量

```sh
sudo fio --name=test_iops --directory=/home/temp --numjobs=8 \
--size=10G --time_based --runtime=60s --ramp_time=2s --ioengine=libaio \
--direct=1 --verify=0 --bs=1M --iodepth=64 --rw=rw \
--group_reporting=1 \
--iodepth_batch_submit=64 --iodepth_batch_complete_max=64
```



执行随机写入来测试写入 IOPS，使用 4 KB 的 I/O 块大小和至少 256 的 I/O 深度

```sh
sudo fio --name=test_iops --directory=/home/temp --numjobs=8 --size=10G \
--time_based --runtime=60s --ramp_time=2s --ioengine=libaio --direct=1 \
--verify=0 --bs=4K --iodepth=256 --rw=randwrite --group_reporting=1  \
--iodepth_batch_submit=256  --iodepth_batch_complete_max=256
```

执行随机读取来测试读取 IOPS，使用 4 KB 的 I/O 块大小和至少 256 的 I/O 深度

```sh
sudo fio --name=test_iops --directory=/home/temp --numjobs=8 --size=10G \
--time_based --runtime=60s --ramp_time=2s --ioengine=libaio --direct=1 \
--verify=0 --bs=4K --iodepth=256 --rw=randread --group_reporting=1 \
--iodepth_batch_submit=256  --iodepth_batch_complete_max=256
```

执行随机读写来测试读写 IOPS，使用 4 KB 的 I/O 块大小和至少 256 的 I/O 深度

```sh
sudo fio --name=test_iops --directory=/home/temp --numjobs=8 --size=10G \
--time_based --runtime=60s --ramp_time=2s --ioengine=libaio --direct=1 \
--verify=0 --bs=4K --iodepth=256 --rw=randrw --group_reporting=1 \
--iodepth_batch_submit=256  --iodepth_batch_complete_max=256
```



清除测试数据

```sh
sudo rm -rf /home/temp/*
```



## `sysbench`作为基准工具测试`I/O`性能（todo 未做实验）

> 参考 [链接](https://www.howtoforge.com/how-to-benchmark-your-system-cpu-file-io-mysql-with-sysbench)

为了测量文件 I/O 性能，我们首先需要创建一个比 RAM 大得多的测试文件（否则系统将使用 RAM 进行缓存，从而篡改基准测试结果） - 150GB 是一个不错的值

```sh
sysbench fileio --file-total-size=50G --file-test-mode=rndrw --time=300 --max-requests=0 prepare
```

运行测试

```sh
sysbench fileio --file-total-size=50G --file-test-mode=rndrw --time=300 --max-requests=0 run
```

删除测试文件

```sh
sysbench fileio --file-total-size=50G --file-test-mode=rndrw --time=300 --max-requests=0 cleanup
```

备份（todo 未做实验）

```bash
# sysbench随机读性能测试
sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndrd --max-time=30 --max-requests=0 prepare

sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndrd --max-time=30 --max-requests=0 run

sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndrd --max-time=30 --max-requests=0 cleanup

# sysbench随机写性能测试
sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndwr --max-time=30 --max-requests=0 prepare

sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndwr --max-time=30 --max-requests=0 run

sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndwr --max-time=30 --max-requests=0 cleanup

# sysbench随机读写性能
sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndrw --max-time=30 --max-requests=0 prepare

sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndrw --max-time=30 --max-requests=0 run

sysbench --test=fileio --file-num=10 --num-threads=16 --file-total-size=10G --file-test-mode=rndrw --max-time=30 --max-requests=0 cleanup

# 使用sysbench产生io负载和分析
# 准备文件准备随机读写io测试
sysbench --test=fileio --file-total-size=20G prepare
# 开始随机读写io测试
sysbench --test=fileio --file-total-size=20G --file-test-mode=rndrw --max-time=300 --max-requests=0 --threads=10 run
# 清除随机读写io测试文件
sysbench --test=fileio --file-total-size=20G cleanup
```

## `I/O`负载监控和分析

### 使用`prometheus`监控`I/O`情况

运行测试步骤如下：

1. 参考 <a href="/prometheus-grafana-alertmanager/使用docker-compose运行prometheus.html" target="_blank">链接</a> 运行`prometheus+node_exporter+process_exporter`监控
1. 通过`node_exporter`的`Storage Disk`试图中的各项指标（重点关注`Instantaneous Queue Size`和`Disk Average Wait Time`指标是否不为0，是则表示有`I/O`请求在排队和`I/O`需要等待，`I/O`设备极度繁忙）综合分析判断`I/O`是否达到瓶颈。

### 使用`iotop`命令分析`I/O`情况

> 结论：如果`IO>`百分比比较大说明`io`等待比较多，表明磁盘`I/O`达到瓶颈。

使用`iotop`命令查看`io`信息

```bash
# 使用iotop显示io线程使用状况
# 安装iotop
yum install iotop -y

# 显示所有线程io状况
iotop
```

`iotop`命令输出示例解析

```bash
Total DISK READ :      45.66 M/s | Total DISK WRITE :	   44.70 M/s
Actual DISK READ:      45.67 M/s | Actual DISK WRITE:	   44.76 M/s
    TID  PRIO  USER     DISK READ  DISK WRITE  SWAPIN     IO>    COMMAND                                                                                                                                           
   4433 be/4 root	 5.52 M/s    5.67 M/s  0.00 % 82.95 % fio --name=test_iops --directory=/home/temp --numjobs=8 --size=10G --time_~up_reporting=1 --iodepth_batch_submit=256 --iodepth_batch_complete_max=256
   4434 be/4 root        5.74 M/s    5.43 M/s  0.00 % 82.75 % fio --name=test_iops --directory=/home/temp --numjobs=8 --size=10G --time_~up_reporting=1 --iodepth_batch_submit=256 --iodepth_batch_complete_max=256
   4435 be/4 root        4.95 M/s    4.72 M/s  0.00 % 82.54 % fio --name=test_iops --directory=/home/temp --numjobs=8 --size=10G --time_~up_reporting=1 --iodepth_batch_submit=256 --iodepth_batch_complete_max=256
   4431 be/4 root        5.06 M/s    4.83 M/s  0.00 % 82.53 % fio --name=test_iops --directory=/home/temp --numjobs=8 --size=10G --time_~up_reporting=1 --iodepth_batch_submit=256 --iodepth_batch_complete_max=256
   4429 be/4 root        6.01 M/s    5.91 M/s  0.00 % 82.11 % fio --name=test_iops --directory=/home/temp --numjobs=8 --size=10G --time_~up_reporting=1 --iodepth_batch_submit=256 --iodepth_batch_complete_max=256
   4428 be/4 root        5.15 M/s    5.02 M/s  0.00 % 81.96 % fio --name=test_iops --directory=/home/temp --numjobs=8 --size=10G --time_~up_reporting=1 --iodepth_batch_submit=256 --iodepth_batch_complete_max=256
   4432 be/4 root        7.24 M/s    7.23 M/s  0.00 % 81.74 % fio --name=test_iops --directory=/home/temp --numjobs=8 --size=10G --time_~up_reporting=1 --iodepth_batch_submit=256 --iodepth_batch_complete_max=256
   4430 be/4 root        6.00 M/s    5.89 M/s  0.00 % 81.58 % fio --name=test_iops --directory=/home/temp --numjobs=8 --size=10G --time_~up_reporting=1 --iodepth_batch_submit=256 --iodepth_batch_complete_max=256
```

- **Total DISK READ 和 Total DISK WRITE**：这是所有作业加起来的总读写速度，分别为45.66 MB/s和44.70 MB/s。
- **Actual DISK READ 和 Actual DISK WRITE**：这里似乎是对总读写速度的一个稍微不同的测量或计算，但差异很小，分别为45.67 MB/s和44.76 MB/s。
- 每个作业（即每个`fio`进程）都显示了自己的读写速率和IO等待时间（`IO>`列，以百分比表示）。从数据可以看出：
  - 大多数作业的读写速率在5 MB/s到7 MB/s之间，但有一个作业（TID 4432）的读写速率较高，分别为7.24 MB/s和7.23 MB/s。
  - 所有作业的IO等待时间都很高（约81%到83%），这表明磁盘I/O是这些进程的主要瓶颈。
- 你的`fio`测试显示了磁盘I/O是这些进程的主要瓶颈。通过调整测试参数、优化系统配置或升级硬件，你可以尝试提高I/O性能。

### 使用`iostat`命令分析`I/O`情况

使用`iostat`命令查看`io`信息

```bash
# 安装iostat
yum install sysstat -y

# 每2秒显示一次io情况，-h表示使用适当的计量单位输出io信息
iostat -h 2
```

`iostat`输出示例解析

```bash
avg-cpu:  %user   %nice %system %iowait  %steal   %idle
           5.8%    0.0%   18.6%   61.8%    0.0%   13.8%

      tps    kB_read/s    kB_wrtn/s    kB_read    kB_wrtn Device
 19747.50        38.4M        38.7M      76.9M      77.4M nvme0n1
     0.00         0.0k         0.0k       0.0k       0.0k scd0
     0.00         0.0k         0.0k       0.0k       0.0k dm-0
     0.00         0.0k         0.0k       0.0k       0.0k dm-1
 19741.00        38.4M        38.7M      76.9M      77.4M dm-2
```

从提供的`iostat`输出来看，系统的CPU使用情况和磁盘I/O性能有一些值得注意的地方。以下是对这些数据的详细解读：

CPU使用情况

- **%user**: 5.8% 表示CPU时间中用户进程所占的百分比。这个值相对较低，通常意味着用户进程没有过多地占用CPU资源。
- **%nice**: 0.0% 表示通过`nice`命令降低优先级的用户进程所占用的CPU时间百分比。这个值为0表示没有使用`nice`调整优先级的进程在运行或它们没有占用显著的CPU时间。
- **%system**: 18.6% 表示系统进程（如内核线程）所占用的CPU时间百分比。这个值适中，但相对于`%iowait`来说，它表明系统本身也在处理一些任务，但这些任务没有被I/O等待所阻塞。
- **%iowait**: 61.8% 是非常高的，表示CPU在空闲时等待I/O操作完成的时间百分比。这通常指示磁盘I/O性能是系统的一个瓶颈。
- **%steal**: 0.0% 表示虚拟化环境中，由于另一个虚拟CPU正在运行而使得当前虚拟CPU处于空闲状态的时间百分比。在你的系统中，这个值为0，表示你可能不是在虚拟化环境中运行，或者虚拟化环境没有造成显著的CPU资源窃取。
- **%idle**: 13.8% 表示CPU处于空闲状态的时间百分比。由于`%iowait`非常高，这里的空闲时间实际上是由于I/O等待造成的“虚假”空闲。

磁盘I/O性能

- **nvme0n1**: 这个设备（很可能是一个NVMe SSD）展示了非常高的吞吐量（tps=19747.50），并且读写速度也相当快（kB_read/s 和 kB_wrtn/s 分别为38.4M和38.7M）。这表明该设备本身性能非常好，但系统的高`%iowait`可能意味着有其他因素（如系统配置、并发I/O请求数、磁盘碎片等）在影响整体性能。
- **scd0, dm-0, dm-1**: 这些设备（可能是光盘驱动器、LVM逻辑卷等）几乎没有I/O活动，这通常是正常的。
- **dm-2**: 这个设备（可能是一个LVM逻辑卷或其他类型的磁盘映射）的I/O性能与nvme0n1相似，但同样受到系统高`%iowait`的影响。

结论与建议

1. **优化I/O性能**：由于`%iowait`非常高，你应该检查是否有大量的小文件I/O请求、磁盘碎片、不合理的I/O调度器设置或其他可能导致I/O瓶颈的因素。考虑使用更合适的I/O调度器（如`noop`、`deadline`或`mq-deadline`对于SSD），或者增加磁盘缓存的大小。
2. **分析应用程序**：查看是否有应用程序正在进行大量的磁盘I/O操作，特别是那些可能导致高`%iowait`的随机I/O请求。优化这些应用程序的I/O模式或使用更快的存储设备可能会有所帮助。
3. **监控和日志**：使用其他工具（如`vmstat`、`pidstat`、`iotop`等）来进一步分析系统的I/O行为和性能瓶颈。检查系统日志以查找任何可能导致I/O性能下降的错误或警告。
4. **硬件升级**：如果可能的话，考虑升级到更快的存储设备或增加更多的存储设备以分散I/O负载。对于NVMe SSD来说，这通常不是必要的，但如果是传统的HDD或SATA SSD，则可能是一个值得考虑的选择。



### 怎么判断`I/O`设备性能已经达到瓶颈呢？

在`I/O CPU`利用率达到100%，`I/O ops`和速率都达到硬盘的峰值时，`I/O`等待队列（`queue size`）不为0代表有`I/O`请求在排队，硬盘`I/O`平均等待时间不为0，表示`I/O`设备性能已经达到瓶颈。



## 模拟过载的`I/O`压力

> 注意：`--iodepth`不适宜大于512，否则会导致`fio`程序卡死无法正常停止。

通过按照16、32、64、128、512、1024顺序逐步调整`--bs`值以达到当前`iops`值小于`--iodepth`值时即可以模拟`I/O`压力过载情况，此时无法通过`ssh`连接主机（`ssh`连接一直卡住）。

```bash
sudo fio --name=test_iops --directory=/home/temp --numjobs=8 --size=10G \
--time_based --runtime=1800s --ramp_time=2s --ioengine=libaio --direct=1 \
--verify=0 --bs=512K --iodepth=512 --rw=randread --group_reporting=1 \
--iodepth_batch_submit=256  --iodepth_batch_complete_max=256
```

## 基于`docker`模拟过载的`I/O`压力

> 用于辅助测试`prometheus+process_exporter`监控方案是否能够收集容器内的进程指标。

`Dockerfile`内容如下：

```dockerfile
# 使用一个包含fio的基础镜像，如ubuntu或其他Linux发行版  
FROM ubuntu:latest  
  
# 安装fio  
RUN apt-get update && apt-get install -y fio  
  
# 设置工作目录（可选）  
WORKDIR /root  
  
# 当你运行容器时，执行的命令（可选）  
# CMD ["fio", "--version"]  # 例如，只打印fio版本
```

编译`docker`镜像

```bash
docker build -t my-fio-image .
```

运行`docker`容器模拟`I/O`过载

```bash
mkdir -p /home/temp &&
docker run --rm -v /home/temp:/mnt/temp my-fio-image \
    fio --name=test_iops --directory=/mnt/temp --numjobs=8 --size=10G \
    --time_based --runtime=1800s --ramp_time=2s --ioengine=libaio --direct=1 \
    --verify=0 --bs=32K --iodepth=512 --rw=randread --group_reporting=1 \
    --iodepth_batch_submit=256  --iodepth_batch_complete_max=256
```

