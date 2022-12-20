#!/bin/bash

# shell判断命令是否执行成功

# 执行which命令，使用$?判断which命令是否执行成功
# https://askubuntu.com/questions/29370/how-to-check-if-a-command-succeeded
which nginx &>/dev/null
if [ $? -eq 0 ]; then
	echo "which nginx &>/dev/null命令执行成功"
else
        echo "which nginx &>/dev/null命令执行失败"
fi

which java &>/dev/null
varResult=$?
if [ $varResult -eq 0 ]; then
        echo "which java &>/dev/null命令执行成功"
else
        echo "which java &>/dev/null命令执行失败"
fi

if which nginx &>/dev/null; then
        echo "which nginx &>/dev/null命令执行成功"
else
        echo "which nginx &>/dev/null命令执行失败"
fi

if which java &>/dev/null; then
        echo "which java &>/dev/null命令执行成功"
else
        echo "which java &>/dev/null命令执行失败"
fi

