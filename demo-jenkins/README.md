## 使用docker-compose运行jenkins

> https://hub.docker.com/r/jenkins/jenkins
> https://github.com/jenkinsci/docker/blob/master/README.md

```
# 启动容器
docker-compose up -d

# 修改jenkins插件下载地址指向国内清华镜像
# NOTE：必需要等待jenkins启动完毕生成/var/jenkins_home/updates目录后才能执行替换命令，有时候需要等待稍长时间jenkins才会自动生成此目录
sed -i 's/http:\/\/updates.jenkins-ci.org\/download/https:\/\/mirrors.tuna.tsinghua.edu.cn\/jenkins/g' /var/jenkins_home/updates/default.json && sed -i 's/http:\/\/www.google.com/https:\/\/www.baidu.com/g' /var/jenkins_home/updates/default.json

# 关闭容器
docker-compose down
```

