########################################################################
#	> File Name:build.sh
#	> Author: dexterleslie
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 日 11/13 01:19:34 2022
########################################################################

#!/bin/bash

rm -f test

rm -rf lib
rm -rf include

mkdir lib
mkdir include

cp ../demo-library/libmylib.so ./lib/
cp ../demo-library/mylib.h ./include/

gcc main.c -l mylib -L ./lib -o test -I ./include/

# 发布libmylib.so到/lib/，否则在运行 test 程序时报告找不到动态链接库错误
sudo cp lib/libmylib.so /lib/
