#!/bin/bash

# 导出环境变量，用于goreleaser trimpath
export DCLI_CURRENT_WORKDIR=`cd .. && pwd`

# 编译发布程序
goreleaser release --snapshot --rm-dist
