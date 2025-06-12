# `Ansible`



## 安装

参考 <a href="/dcli/README.html#安装" target="_blank">链接</a> 安装 dcli 程序

安装 `Ansible`

```bash
sudo dcli ansible install
```

查看 `Ansible` 版本以检查是否成功安装

```sh
ansible --version
```



## 运行 `playbook` 命令

`playbook.yml` 内容如下：

```yaml
# hosts=all表示支持命令行执行playbook时使用--inventory指定hosts
- hosts: all
  tasks:
   - name: "find /home子目录"
     find:
      paths: /home
      file_type: directory
     register: varFiles
   - name: "打印/home子目录"
     debug:
      msg: "{{ item.path.split('/').2 }}"
     with_items: 
      - "{{ varFiles.files }}"
```

```sh
# 使用 yml 配置的 hosts 或者 /etc/ansible/hosts 配置的 hosts 运行 playbook
ansible-playbook playbook.yml

# 指定 hosts 和 SSH 信息远程执行 playbook，注意：--inventory 最后一定要有逗号，否则报错
ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook playbook.yml --inventory 192.168.1.150,192.168.1.151, --user root -e ansible_ssh_pass="xxx"
```



## 变量



### 在 `playbook` 中定义变量

可以在 Playbook 的 `vars` 字段中直接定义变量。

`playbook.yml` 内容如下：

```yaml
- hosts: all
  vars:
    my_var1: v1
    my_var2: v2
  tasks:
    - debug:
        msg: "my_var1={{my_var1}},my_var2={{my_var2}}"

```

- 在 `vars` 字段中定义变量后，可以在任务中使用 `{{ variable_name }}` 的语法引用变量。

执行命令

```sh
ansible-playbook playbook.yml --inventory localhost,
```



### 在 `inventory` 文件中定义变量

可以在 Inventory 文件中为主机或主机组定义变量。

Inventory 文件（`inventory.ini`）：

```
[testservers]
localhost
 
# 定义 testservers 主机组才能够使用的变量
[testservers:vars]
my_var1=v1
my_var2=v2

# 定义全局变量，所有主机组都能够使用
[all:vars]
ansible_user=root
ansible_ssh_pass=Root@123
```

`playbook.yml`：

```yaml
- hosts: all
  tasks:
    - debug:
        msg: "my_var1={{my_var1}},my_var2={{my_var2}}"
```

执行命令

```sh
ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook playbook.yml --inventory inventory.ini
```



### 使用 `vars_files` 引入外部变量文件

`vars.yml`：

```yaml
my_var1: v1
my_var2: v2
```

`playbook.yml`：

```yaml
- hosts: all
  vars_files:
    - vars.yml
  tasks:
    - debug:
        msg: "my_var1={{my_var1}},my_var2={{my_var2}}"

```

调试命令

```sh
ansible-playbook playbook.yml --inventory localhost,
```



### 在命令行中通过 `--extra-vars` 传递变量

可以通过命令行直接传递变量，覆盖 Playbook 或 Inventory 中定义的变量。

`playbook.yml`：

```yaml
- hosts: all
  tasks:
    - debug:
        msg: "my_var1={{my_var1}},my_var2={{my_var2}}"

```

- `--extra-vars` 传递的变量优先级最高，会覆盖 Playbook 或 Inventory 中定义的同名变量。

调试命令

```sh
ansible-playbook playbook.yml --inventory localhost, --extra-vars "my_var1=v1 my_var2=v2"
```



### 使用 `set_fact` 动态设置变量

可以在任务中使用 `set_fact` 模块动态设置变量。`set_fact` 适用于在 Playbook 运行过程中动态生成变量。

`playbook.yml`：

```yaml
- hosts: all
  tasks:
    - set_fact:
        my_var1: v1
        my_var2: v2
    - debug:
        msg: "my_var1={{my_var1}},my_var2={{my_var2}}"
```

调试命令

```sh
ansible-playbook playbook.yml --inventory localhost,
```



### 角色（Roles）中定义变量

目录结构：

```
roles
├── my_role
│   ├── tasks
│   │   └── main.yml
│   └── vars
│       └── main.yml
└── playbook.yml
```

`my_role/vars/main.yml`：

```yaml
my_var1: v1
my_var2: v2

```

`my_role/tasks/main.yml`：

```yaml
- debug:
    msg: "my_var1={{my_var1}},my_var2={{my_var2}}"

```

`roles/playbook.yml`：

```yaml
- hosts: all
  roles:
    - my_role

```

调试命令

```sh
ansible-playbook playbook.yml --inventory localhost,
```



### 使用 `vars_prompt` 交互式输入变量

可以在 Playbook 中使用 `vars_prompt` 提示用户输入变量。`vars_prompt` 适用于需要用户交互的场景。

`playbook.yml`：

```yaml
- hosts: all
  vars_prompt:
    - name: my_var1
      prompt: "Enter my_var1"
      private: False
    - name: my_var2
      prompt: "Enter my_var2"
      private: False
  tasks:
    - debug:
        msg: "my_var1={{my_var1}},my_var2={{my_var2}}"
```

调试命令

```sh
ansible-playbook playbook.yml --inventory localhost,
```



### 使用 `include_vars` 动态加载变量文件

可以在任务中使用 `include_vars` 动态加载变量文件。`include_vars` 适用于在 Playbook 运行过程中动态加载变量文件。

`vars.yml`：

```yaml
my_var1: v1
my_var2: v2
```

`playbook.yml`：

```yaml
- hosts: all
  tasks:
    - include_vars:
        file: vars.yml
    - debug:
        msg: "my_var1={{my_var1}},my_var2={{my_var2}}"

```

调试命令

```sh
ansible-playbook playbook.yml --inventory localhost,
```



## 指定主机的方式

### 使用 `/etc/ansible/hosts` 指定主机



#### `/etc/ansible/hosts` 中所有主机

`playbook.yml` 内容如下：

```yaml
- hosts: all
  tasks:
   - name: "find /home子目录"
     find:
      paths: /home
      file_type: directory
     register: varFiles
   - name: "打印/home子目录"
     debug:
      msg: "{{ item.path.split('/').2 }}"
     with_items: 
      - "{{ varFiles.files }}"
```

执行下面命令会连接 `/etc/ansible/hosts` 中配置的所有主机

```sh
ansible-playbook playbook.yml
```



#### `/etc/ansible/hosts` 中指定的主机组

`/etc/ansible/hosts` 内容如下：

```
[test1]
192.168.1.182 ansible_user=root ansible_ssh_pass=xxx
192.168.1.183 ansible_user=root ansible_ssh_pass=xxx
```

`playbook.yml` 内容如下：

```yaml
# 指定 /etc/ansible/hosts 中的 test1 主机组
- hosts: test1
  tasks:
   - name: "find /home子目录"
     find:
      paths: /home
      file_type: directory
     register: varFiles
   - name: "打印/home子目录"
     debug:
      msg: "{{ item.path.split('/').2 }}"
     with_items: 
      - "{{ varFiles.files }}"
```

执行命令

```sh
ansible-playbook playbook.yml
```



### 命令行 `--inventory` 指定主机 `ip` 地址

`playbook.yml` 内容如下：

```yaml
# hosts=all 表示支持命令行执行 playbook 时使用 --inventory 指定主机列表
- hosts: all
  tasks:
   - name: "find /home子目录"
     find:
      paths: /home
      file_type: directory
     register: varFiles
   - name: "打印/home子目录"
     debug:
      msg: "{{ item.path.split('/').2 }}"
     with_items: 
      - "{{ varFiles.files }}"
```

使用 `--inventory` 指定主机列表

```sh
ansible-playbook playbook.yml --inventory 192.168.1.150,192.168.1.151, --user root -e ansible_ssh_pass="xxx"
```



### 命令行 `--inventory` 指定主机配置文件

`inventory.ini` 内容如下：

```
[test1]
192.168.1.182 ansible_user=root ansible_ssh_pass=xxx
192.168.1.183 ansible_user=root ansible_ssh_pass=xxx
```

`playbook.yml` 内容如下：

```yaml
# 指定 test1 主机组
- hosts: test1
  tasks:
   - name: "find /home子目录"
     find:
      paths: /home
      file_type: directory
     register: varFiles
   - name: "打印/home子目录"
     debug:
      msg: "{{ item.path.split('/').2 }}"
     with_items: 
      - "{{ varFiles.files }}"
```

执行命令

```sh
ansible-playbook playbook.yml --inventory inventory.ini
```

- `--inventory inventory.ini` 指定主机配置文件。



## `debug` 输出命令执行结果

`playbook.yml` 内容如下：

```yaml
- hosts: all
  tasks:
    # 执行date命令
    - name: date
      shell: date
      # 暂存date命令结果
      register: date_output
    # 显示date命令执行结果
    - name: date命令结果
      debug:
        var: date_output.stdout

```

执行 `playbook`

```sh
ansible-playbook playbook.yml --inventory 192.168.1.181, --user root
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

