# ansible



## 安装

参考 <a href="/dcli/README.html#安装" target="_blank">链接</a> 安装 dcli 程序

安装 ansible

```bash
sudo dcli ansible install
```



## 运行playbook命令

```
# 使用yml配置的hosts或者/etc/ansible/hosts配置的hosts运行playbook
ansible-playbook playbook.yml

# 指定hosts和SSH信息远程执行playbook，NOTE: --inventory最后一定要有逗号，否则报错
ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook demo-cli-specify-hosts.yml --inventory 192.168.1.150,192.168.1.151, --user root -e ansible_ssh_pass="xxx"
```



## Including and Importing

> https://docs.ansible.com/ansible/2.9/user_guide/playbooks_reuse_includes.html
> 参考: including-and-importing demo



## patterns和/etc/ansible/hosts配置

> https://stackoverflow.com/questions/34334377/how-to-specify-user-name-in-host-file-of-ansible
> https://docs.ansible.com/ansible/latest/inventory_guide/intro_patterns.html#intro-patterns

```
# /etc/ansible/hosts样板
[demoservers]
192.168.1.187 ansible_user=root ansible_ssh_pass=xxx
192.168.1.188 ansible_user=root ansible_ssh_pass=xxx

# 执行ad-hoc命令时指定主机组
ansible demoservers -m shell -a "date"
```



## ad-hoc 方式执行命令

> [参考链接](https://docs.ansible.com/ansible/latest/command_guide/intro_adhoc.html)

指定执行命令的主机 IP

>[参考链接](https://stackoverflow.com/questions/17188147/how-to-run-ansible-without-specifying-the-inventory-but-the-host-directly)

```bash
ansible all -i 192.168.1.187,192.168.1.188, -k -m shell -a "date"
```

- -i, --inventory 指定清单主机路径或逗号分隔的主机列表
- -k, --ask-pass 询问连接密码



指定执行命令的主机组，demoservers 在 /etc/ansible/hosts 已经配置的主机组

```bash
ansible demoservers -m shell -a "date"
```



## 异步模式

>
> NOTE: 暂时没有需求使用，不研究。
> https://stackoverflow.com/questions/41194021/how-can-i-show-progress-for-a-long-running-ansible-task
> https://docs.ansible.com/ansible/latest/playbook_guide/playbooks_async.html



### ad-hoc异步

```
# 使用异步模式执行ad-hoc模式
# -B 'SECONDS', --background 'SECONDS': run asynchronously, failing after X seconds (default=N/A)
# -P 'POLL_INTERVAL', --poll 'POLL_INTERVAL': set the poll interval if using -B (default=15)
ansible all -B 60 -P 2 -m shell -a "while true; do echo `date`; sleep 2; done"

# 查看异步模式job状态
ansible all -m async_status -a "jid=185559806748.13487"
```





## 模块



### shell模块

> NOTE: ad-hoc方式不能指定shell模块参数，否则ansible没有结果输出，https://github.com/ansible/ansible/issues/73005
>
> https://docs.ansible.com/ansible/latest/collections/ansible/builtin/shell_module.html

```
# 执行命令
ansible demoservers -m shell -a "echo `date`; echo 'Sleep for 5 seconds...'; sleep 5; echo `date`"
```



### script模块

> 此模块会把本地的shell脚本传输到远程服务器后执行脚本。
> https://gist.github.com/ericwastaken/c98d2ea8b1752f9903ce93df5847905a

```
# 在ansible主机创建脚本文件demo.sh，内容如下:
i=0 
while [ $i -lt 3 ]; 
do 
	echo `date`
       	sleep 2
	((i=i+1))
done

# 调用script模块
ansible demoservers -m script -a "demo.sh"
```

