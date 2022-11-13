########################################################################
#	> File Name:build.sh
#	> Author: dexterleslie
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 日 11/13 01:19:34 2022
########################################################################

#!/bin/bash

rm -f test

cp ../demo-library/libmylib.so ./lib/
cp ../demo-library/mylib.h ./include/

gcc main.c -l mylib -L ./lib -o test -I ./include/
