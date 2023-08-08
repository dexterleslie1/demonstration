# ansible使用

## 配置ansible环境

> 使用dcli setup.sh安装并配置ansible环境



## 运行playbook命令

```
# 使用yml配置的hosts或者/etc/ansible/hosts配置的hosts运行playbook
ansible-playbook playbook.yml

# 指定hosts和SSH信息远程执行playbook，NOTE: --inventory最后一定要有逗号，否则报错
ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook demo-cli-specify-hosts.yml --inventory 192.168.1.150,192.168.1.151, --user root -e ansible_ssh_pass="xxx"
```

