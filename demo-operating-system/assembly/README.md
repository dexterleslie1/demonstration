# 汇编语言学习

## 配置调试开发环境

使用DOSBox模拟器调试汇编程序。

### macOS安装DOSBox

参考：https://blog.csdn.net/weixin_38633659/article/details/125154807

- 解压《汇编语言(第3版) 》王爽著.zip

- 运行DOSBox-0.74-3-3.dmg

- 解压DOSBox/tools.zip后复制DOSBox/masm5目录下所有文件到自定义目录~/assembly，里面的工具用于编译汇编源代码

- 在DOSBox中挂载~/assembly到c:盘

  ```shell
  mount c: ~/assembly
  ```

#### ubuntu安装DOSBox

- apt安装dosbox

  ```shell
  sudo apt install dosbox
  ```

- 解压DOSBox/tools.zip后复制DOSBox/masm5目录下所有文件到自定义目录~/assembly，里面的工具用于编译汇编源代码

- 在DOSBox中挂载~/assembly到c:盘

  ```shell
  mount c: ~/assembly
  ```

### 编译、链接、运行

```shell
# 编译源代码生成obj文件，注意：分号;表示不需要交互式编译
masm test.asm;

# 链接obj文件生成exe文件
link test;

# 运行程序
test
```

### DOSBox debug程序调试

参考：https://montcs.bloomu.edu/Information/LowLevel/DOS-Debug.html

#### -r

打印寄存器内容

#### -g 076a:000b

在地址076a:000b处打断点并让程序继续执行到断点处

#### -d 076a:000b

以16进制显示地址076a:000b处的内存内容

#### -u

反汇编cs:ip开始处指令

#### -t

step over