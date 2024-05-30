#!/bin/bash

# 可以重复执行这个脚本
# 软链接外部文档到 docs 中
# (cd docs && rm -rf 英语学习 && ln -s ../../../demo-english 英语学习 || exit 0)
# (cd docs && rm -rf intellij-idea && ln -s ../../../demo-idea intellij-idea || exit 0)
# (cd docs && rm -rf MySQL或MariaDB学习 && ln -s ../../../demo-mysql MySQL或MariaDB学习 || exit 0)
# (cd docs && rm -rf docker容器 && ln -s ../../../docker docker容器 || exit 0)
# # (cd docs && rm -rf React技术 && ln -s ../../../front-end/demo-reactjs React技术 || exit 0)
# (cd docs && rm -rf shell脚本编程 && ln -s ../../../demo-shell-scripting shell脚本编程 || exit 0)

# 定义源目录到目标目录的对照
declare -A source_dir_to_target_dir_map
source_dir_to_target_dir_map["../../demo-english"]="英语学习"
source_dir_to_target_dir_map["../../demo-idea"]="intellij-idea"
source_dir_to_target_dir_map["../../demo-mysql"]="MySQL"
source_dir_to_target_dir_map["../../demo-mariadb"]="MariaDB"
source_dir_to_target_dir_map["../../docker"]="docker容器"
source_dir_to_target_dir_map["../../front-end/demo-reactjs"]="React技术"
source_dir_to_target_dir_map["../../demo-shell-scripting"]="shell脚本编程"
source_dir_to_target_dir_map["../../linux/ubuntu"]="ubuntu使用"
source_dir_to_target_dir_map["../../linux"]="linux使用"
source_dir_to_target_dir_map["../../demo-benchmark"]="基准和性能测试"
source_dir_to_target_dir_map["../../front-end/html+js+css/demo-less-sass-scss-postcss"]="less-sass-postcss"
source_dir_to_target_dir_map["../../front-end/html+js+css/demo-less-sass-scss-postcss/demo-less"]="less"
source_dir_to_target_dir_map["../../front-end/html+js+css/demo-less-sass-scss-postcss/demo-scss"]="scss"
source_dir_to_target_dir_map["../../demo-prometheus-grafana-alertmanager"]="prometheus-grafana-alertmanager"
source_dir_to_target_dir_map["../../demo-prometheus-grafana-alertmanager/demo-grafana"]="grafana"
source_dir_to_target_dir_map["../../demo-prometheus-grafana-alertmanager/demo-prometheus"]="prometheus"
source_dir_to_target_dir_map["../../demo-redis"]="redis"
source_dir_to_target_dir_map["../../demo-mybatis"]="mybatis"
source_dir_to_target_dir_map["../../demo-maven"]="maven"
source_dir_to_target_dir_map["../../demo-git"]="git"
source_dir_to_target_dir_map["../../demo-mockito"]="mockito"
source_dir_to_target_dir_map["../../demo-spring-boot"]="spring-boot"

for key in ${!source_dir_to_target_dir_map[@]}; do
    # 删除符号链接目录
    rm -rf docs/${source_dir_to_target_dir_map[$key]}
    mkdir -p docs/${source_dir_to_target_dir_map[$key]}
    # 查询源目录中 *.md 文件
    md_file_list=(`find $key -maxdepth 1 -iname "*\.md" -printf '%f\n'`)

    # 创建所有 *.md 文件软链接到 docs 中
    for md_file in ${md_file_list[@]}; do
        ln -s ../../$key/$md_file docs/${source_dir_to_target_dir_map[$key]}/$md_file
    done
done
