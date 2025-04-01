# https://unix.stackexchange.com/questions/644343/bash-while-loop-stop-after-a-successful-curl-request
#!/bin/bash

# target_domain=www.baidu.com
target_domain=x79878899.com

while ! curl -s -f -o /dev/null --connect-timeout 5 $target_domain;
do
 echo "`date` - 命令curl -s -f -o /dev/null --connect-timeout 5 $target_domain 执行失败重试"
 sleep 1
done

echo "`date` - 命令curl -s -f -o /dev/null --connect-timeout 5 $target_domain 成功执行"
