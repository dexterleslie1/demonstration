# `goreleaser`

> [官方goreleaser文档（参考官方资料就足够）](https://goreleaser.com/intro/)



## 安装

### `MacOS`

```bash
brew install goreleaser

goreleaser --version
```



### `CentOS8`

```shell
# 参考 https://goreleaser.com/install/#yum

# 设置yum repo
echo '[goreleaser]
name=GoReleaser
baseurl=https://repo.goreleaser.com/yum/
enabled=1
gpgcheck=0' | sudo tee /etc/yum.repos.d/goreleaser.repo

# 安装goreleaser
sudo yum install goreleaser -y
```



### `Ubuntu`

```bash
sudo snap install goreleaser --classic
```



## 使用

`goreleaser`详细配置和用法请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-golang/demo-goreleaser)

```bash
# 在当前目录生成.goreleaser.yaml文件
goreleaser init

# 使用snapshot在本地编译并且打包binary，不使用github发布，--rm-dist删除./dist目录
# 注意：新版本的goreleaser使用--clean代替--rm-dist
goreleaser release --snapshot --rm-dist
```