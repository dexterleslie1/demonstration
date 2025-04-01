# 演示cobra使用

## 参考资料

**[cobra详细文档](https://umarcor.github.io/cobra/#overview)**

**[bilibili视频](https://www.bilibili.com/video/BV1ka4y177iK?spm_id_from=333.999.0.0&vd_source=872f70ee43293e5dbba94e3aecc154d2)**

**[官方安装和使用参考](https://github.com/spf13/cobra)**

## cobra环境安装和配置

```
# 安装cobra-cli
go install github.com/spf13/cobra-cli@latest

# 检查
cobra-cli --version

```

## cobra-cli使用

**[官方cobra-cli文档](https://github.com/spf13/cobra-cli/blob/main/README.md)**

```
# 在当前工作目录初始化一个新的cobra项目
cd demo-cobra
cobra-cli init .

# 添加一个命令到项目
cobra-cli add test
```
