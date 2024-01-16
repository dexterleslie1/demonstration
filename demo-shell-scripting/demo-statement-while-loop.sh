#!/bin/bash

echo "------------ while do done 语法 -----------------"
myVar=""
while [ "$myVar" = "124" ] || [ "$myVar" != "PONG" ]
do
    echo "InLoop..."
    myVar="PONG"
    sleep 1
done
