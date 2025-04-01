## 运行demo

> 执行playbook后程序会自动重试提交代码到gitlab

```
# 启动gitlab服务器
docker-compose up -d

# 执行playbook，等待5-10分钟代码成功自动提交到远程
ansible-playbook main.yml
```

