#!/bin/bash

# 演示判断文件夹是否存在
if [ -d test-dir ]
then
    echo 'Directory exists'
else 
    echo 'Directory not exists'
fi
