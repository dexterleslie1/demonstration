#!/bin/bash

# 演示判断文件是否存在
# https://stackoverflow.com/questions/40082346/how-to-check-if-a-file-exists-in-a-shell-script
if [ -e test.txt ] 
then
    echo 'file exists'
else
    echo 'file not exists'
fi

if [ ! -e test.txt ]
then
    echo 'file not exists'
else
    echo 'file exists'
fi
