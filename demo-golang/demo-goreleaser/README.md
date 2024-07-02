# 演示goreleaser使用

## 参考资料

> [官方goreleaser文档（参考官方资料就足够）](https://goreleaser.com/intro/)

## macOS安装goreleaser

```
brew install goreleaser

goreleaser --version
```

## centOS8安装goreleaser

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



## `ubuntu`安装`goreleaser`

```bash
sudo snap install goreleaser --classic
```



## 使用goreleaser

```
# 在当前目录生成.goreleaser.yaml文件
goreleaser init

# 使用snapshot在本地编译并且打包binary，不使用github发布，--rm-dist删除./dist目录
goreleaser release --snapshot --rm-dist
```
