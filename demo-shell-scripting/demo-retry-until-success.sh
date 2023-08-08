#!/bin/bash

# 演示重试命令直到成功

# 使用touch ./.test.log 创建文件并退出shell
while ! ls -lrt ./.test.log;
do
 echo "`date` - 命令ls -lrt ./.test.log执行失败重试"
 sleep 5
done