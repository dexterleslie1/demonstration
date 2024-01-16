#!/bin/bash

# if 语法
echo "------------ if 语法 -----------------"
myVar=""
myVar1=""
if [ "$myVar" = "$myVar1" ]; then
    echo "\$myVar等于\$myVar1"
fi

if [ "$myVar" = "$myVar1" ]
then
    echo "\$myVar等于\$myVar1"
fi



# if else 语法
echo "------------ if else 语法 -----------------"
myVar="x"
myVar1=""
if [ "$myVar" = "$myVar1" ]; then
    echo "\$myVar等于\$myVar1"
else
    echo "\$myVar不等于\$myVar1"
fi
