# 演示goreleaser使用

## 参考资料

> [官方goreleaser文档（参考官方资料就足够）](https://goreleaser.com/intro/)

## macOS安装goreleaser

```
brew install goreleaser

goreleaser --version
```

## 使用goreleaser

```
# 在当前目录生成.goreleaser.yaml文件
goreleaser init

# 使用snapshot在本地编译并且打包binary，不使用github发布，--rm-dist删除./dist目录
goreleaser release --snapshot --rm-dist
```
