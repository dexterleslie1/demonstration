#!/bin/bash

docker-compose up -d

# 自动推送文件到test项目
rm -rf test-temp

while ! git clone http://root:token-string-here123456@localhost/root/test.git test-temp;
do
    echo 'git clone执行失败重试'
    sleep 5
done

cp Jenkinsfile test-temp
(cd test-temp && git add .) || exit $?
(cd test-temp && git commit -m '初始化') || exit $?
(cd test-temp && git push) || exit $?
