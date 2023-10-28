#!/bin/bash

while ! nc -w 5 -z 192.168.1.181 22; do echo "retry ..."; sleep 2; done

echo "端口已经准备就绪"
