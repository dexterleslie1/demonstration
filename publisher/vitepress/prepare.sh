#!/bin/bash

# 可以重复执行这个脚本
# 软链接外部文档到 docs 中

# 定义源目录到目标目录的对照
# declare -A source_dir_to_target_dir_map
# source_dir_to_target_dir_map["../../demo-english"]="英语学习"

# for key in ${!source_dir_to_target_dir_map[@]}; do
#     # 删除符号链接目录
#     rm -rf docs/${source_dir_to_target_dir_map[$key]}
#     mkdir -p docs/${source_dir_to_target_dir_map[$key]}
#     # 查询源目录中 *.md 文件
#     md_file_list=(`find $key -maxdepth 1 -iname "*\.md" -printf '%f\n'`)

#     # 创建所有 *.md 文件软链接到 docs 中
#     for md_file in ${md_file_list[@]}; do
#         ln -s ../../$key/$md_file docs/${source_dir_to_target_dir_map[$key]}/$md_file
#     done
# done
echo "Hello world!!"
pwd
ls -alh .
ls -alh ../
