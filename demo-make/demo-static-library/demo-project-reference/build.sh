########################################################################
#	> File Name:build.sh
#	> Author: dexterleslie
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 日 11/13 01:19:34 2022
########################################################################

#!/bin/bash

rm -f test

cp ../demo-project-library/libmylib.a ./lib/
cp ../demo-project-library/mylib.h ./include/

gcc main.c ./lib/libmylib.a -o test -I ./include/
