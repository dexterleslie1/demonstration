## 调试ansible playbook

```
# 用于调试ansible playbook
ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook config-devops-master.yml --inventory 192.168.1.151, --user root -e ansible_ssh_pass="xxx"
```

