## 使用docker-compose运行gitlab

> https://docs.gitlab.com/omnibus/docker/

```
# 启动gitlab服务
docker-compose up -d

# 需要等待一段时间比较长时间（NOTE:可能10几分钟）启动后访问和配置gitlab，在Admin Area > Settings设置禁止用户sign-up
http://192.168.1.60

# 如果没有使用环境变量修改root密码，可以通过以下方法手动登录并重置root密码
# 登录帐号: root，密码位于容器内的/etc/gitlab/initial_root_password，NOTE: 如果未启动完毕登录会报告帐号和密码错误，导致root无法成功登录
cat /etc/gitlab/initial_root_password

# 关闭gitlab服务
docker-compose down
```

