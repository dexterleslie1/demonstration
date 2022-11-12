########################################################################
#	> File Name:build.sh
#	> Author: dexterleslie
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 日 11/13 01:05:11 2022
########################################################################

#!/bin/bash

rm -f *.o
rm -f *a

gcc -c myadd.c -o myadd.o
gcc -c mysub.c -o mysub.o

ar rcs libmylib.a myadd.o mysub.o
rm -f *o
