#!/bin/bash

# 可以重复执行这个脚本
# 软链接外部文档到 docs 中
(cd docs && rm -rf 英语学习 && ln -s ../../../demo-english 英语学习 || exit 0)
(cd docs && rm -rf intellij-idea && ln -s ../../../demo-idea intellij-idea || exit 0)
(cd docs && rm -rf MySQL或MariaDB学习 && ln -s ../../../demo-mysql MySQL或MariaDB学习 || exit 0)
(cd docs && rm -rf docker容器 && ln -s ../../../docker docker容器 || exit 0)
