#!/bin/bash

echo '启动harbor服务。。。'
(cd harbor && sudo ./install.sh)

echo '启动jenkins、gitlab服务。。。'
docker-compose up -d

# 自动推送文件到test项目
rm -rf test-temp

while ! git clone http://root:token-string-here123456@localhost:50002/root/test.git test-temp;
do
    echo 'git clone执行失败重试'
    sleep 5
done

# 因为创建仓库时initialize_with_readme，偶尔克隆仓库后版本落后于服务器版本，所以休眠一点时间等待initialize_with_readme完毕
sleep 5

cp Jenkinsfile test-temp
(cd test-temp && git add .) || exit $?
(cd test-temp && git commit -m '初始化') || exit $?
(cd test-temp && git push) || exit $?

rm -rf test-temp 
