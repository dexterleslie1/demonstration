#!/bin/bash

# tomcat、nginx等服务重启脚本

# 编辑crontab -uroot -e填写如下内容
# 0 7 */5 * * /data/service-restart.sh >> /var/log/service-restart.log
# 脚本添加执行权限chmod o+x service-restart.sh
# 修改以下变量配置
varTomcatUser=root
varTomcatDir=/data/tomcat

echo "`date` 停止nginx。。。"
service nginx stop

# 关闭tomcat
echo "`date` 关闭tomcat。。。"
#sudo -u $varTomcatUser sh $varTomcatDir/bin/shutdown.sh 1>/dev/null
varTomcatPid=`ps aux | grep $varTomcatDir | grep -v grep | awk '{print $2}'`
if ! [[ "$varTomcatPid" == "" ]]; then
 kill -9 $varTomcatPid
fi

# 启动tomcat
echo "`date` 启动tomcat。。。"
sudo -u $varTomcatUser sh $varTomcatDir/bin/startup.sh 1>/dev/null

# 检查tomcat是否已经启动
while ! curl --connect-timeout 5 --max-time 2 --output /dev/null --silent http://localhost:8080/;
do
 echo "`date` 等待tomcat启动中。。。"
 sleep 1
done

echo "`date` 启动nginx。。。"
service nginx start

