#!/bin/bash

# 演示for循环逻辑使用
read -p "输入循环次数：" varLoopCount
for ((i=1; i<=$varLoopCount; i++))
do
	echo "循环第 $i 次"
done
