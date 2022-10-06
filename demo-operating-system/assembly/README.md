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

  

