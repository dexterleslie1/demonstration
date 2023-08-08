## 运行demo

> 执行playbook后程序会自动重试提交代码到gitlab

```
# 启动gitlab服务器
docker-compose up -d

# 执行playbook
ansible-playbook main.yml
```

