########################################################################
#	> File Name:build.sh
#	> Author: dexterleslie
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 日 11/13 01:05:11 2022
########################################################################

#!/bin/bash

rm -f *.o
rm -f *.so

gcc -c myadd.c -o myadd.o -fPIC
gcc -c mysub.c -o mysub.o -fPIC

gcc -shared -fPIC -o libmylib.so myadd.o mysub.o
rm -f *.o
