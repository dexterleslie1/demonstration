#!/bin/bash

# 编译 website-controller 可执行文件
# 下载 k8s.io/code-generator 到 vendor/k8s.io/code-generator
go mod vendor
# 生成代码 pkg/generated 和 pkg/apis/extensions.example.com/v1/zz_generated.deepcopy.go
hack/update-codegen.sh
go build -o website-controller

# 编译controller镜像
docker build --tag docker.118899.net:10001/yyd-public/website-controller:latest -f Dockerfile .

# 推送controller镜像
docker push docker.118899.net:10001/yyd-public/website-controller:latest
