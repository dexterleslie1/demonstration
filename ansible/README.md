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



## 禁用主机密钥检查

你可以通过在Ansible配置文件中设置`host_key_checking = False`来禁用主机密钥检查。这通常是在`ansible.cfg`文件中完成的。如果你没有自定义的`ansible.cfg`文件，可以在项目目录中创建一个，并添加以下内容：

```ini
[defaults]
host_key_checking = False

```

或者，你可以在运行Ansible命令时通过环境变量禁用主机密钥检查：

```sh
export ANSIBLE_HOST_KEY_CHECKING=False
ansible-playbook playbook.yml --inventory inventory.ini
```

或者

```sh
ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook playbook.yml --inventory inventory.ini
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



## 特殊变量

>[参考官方文档](https://docs.ansible.com/ansible/latest/reference_appendices/special_variables.html)

### 魔法变量 - `groups`

包含清单中所有组的字典/地图，每个组都有属于它的主机列表。

`inventory.ini`：

```ini
[testservers]
192.168.1.90
192.168.1.91
192.168.1.92

# 定义全局变量，所有主机组都能够使用
[all:vars]
ansible_user=root
ansible_ssh_pass=Root@123
```

`playbook.yml`：

```yaml
- hosts: all
  vars:
    my_var: "{{ groups['testservers'][:2] }}"
  tasks:
    - debug:
        var: my_var
```

调试命令

```sh
ansible-playbook playbook.yml --inventory inventory.ini
```



### 魔法变量 - `inventory_hostname`

当前正在迭代的主机的清单名称。这不受委托的影响，它始终反映任务的原始主机。

只在第一个 `Cassandra` 主机中执行 `shell`：

```yaml
- name: "初始化Cassandra数据库和表结构"
  shell: |
    set -e
    cd ~/deployer-flash-sale/cassandra
    docker compose exec node0 sh -c "cqlsh -e \"source '/scripts/data.cql'\""
   when: inventory_hostname == groups['cassandra'][0]
```



## `when` 条件判断

### 判断变量是否定义

Ansible 提供了 `is defined` 测试来检查变量是否已定义。

如果你想检查一个变量是否已经定义，可以使用 `is defined` 测试。

```yaml
- hosts: all
  tasks:
    - name: Print a message if the variable is defined
      debug:
        msg: "The variable my_var is defined."
      when: my_var is defined
```

如果你想在变量未定义时执行某个任务，可以使用 `is not defined`。

```yaml
- hosts: all
  tasks:
    - name: Print a message if the variable is not defined
      debug:
        msg: "The variable my_var is not defined."
      when: my_var is not defined
```



### `and、or` 组合条件

`and` 组合条件

```yaml
- hosts: all
  tasks:
    - name: Print a message if the variable is defined and equals 'yes'
      debug:
        msg: "The variable my_var is defined and equals 'yes'."
      when: my_var is defined and my_var == 'yes'
```



## `delegate_to、run_once`

只在第一个 `Cassandra` 主机中执行一次 `shell`：

```yaml
- name: "初始化Cassandra数据库和表结构"
  shell: |
    set -e
    cd ~/deployer-flash-sale/cassandra
    docker compose exec node0 sh -c "cqlsh -e \"source '/scripts/data.cql'\""
  run_once: true
  delegate_to: "{{ groups['cassandra'][0] }}"
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



## `Jinja2`

### `join`

使用 `join` 过滤器将列表中的元素连接成一个字符串，并用逗号分隔。

```yaml
- hosts: all
  gather_facts: no
  vars:
    # 提取前三个 Cassandra 服务器的 IP 地址
    cassandra_ips: "{{ groups['cassandra'][:3] | join(',') }}"

  tasks:
    - name: Print the first three Cassandra IPs
      debug:
        msg: "The first three Cassandra IPs are: {{ cassandra_ips }}"
```

- `groups['cassandra']`：
  - `groups` 是一个 Ansible 提供的变量，包含所有主机组的信息。
  - `groups['cassandra']` 返回 `cassandra` 组中的所有主机列表。
- `[:3]`:
  - 这是 Python 的切片语法，用于获取列表中的前三个元素。
- `| join(',')`:
  - 使用 `Jinja2` 的 `join` 过滤器将列表中的元素连接成一个字符串，并用逗号分隔。
- `cassandra_ips`:
  - 这是一个自定义变量，用于存储拼接后的 IP 地址字符串。



### `for`

使用 `for` 循环语法动态生成 `nginx upstream` 配置中的主机列表配置：

```jinja2
upstream backend {
    least_conn;
    keepalive 65535;
    keepalive_timeout 65; # 与 Spring Boot 的连接超时对齐
    keepalive_requests 100000; # 单个连接最大请求数
    # server 192.168.1.187:8080;
    {% for host in groups['api'] %}
    server {{ host }}:8080;
    {% endfor %}
}
```

- 循环主机组 `api` 生成 `nginx upstream` 主机列表配置。



使用 `for` 循环语法动态生成 `prometheus` 静态配置：

```yaml
# 搜刮配置
scrape_configs:
  - job_name: "prometheus"
    static_configs:
      - targets: ["prometheus:9090"]
  # openresty metrics端点配置
  - job_name: 'openresty'
    static_configs:
#      - targets: ['192.168.1.185:9145']
#        labels:
#          instance: openresty-1
{% for host in groups['openresty'] %}
       - targets: ['{{ host }}:9145']
         labels:
           instance: openresty-{{ loop.index }}
{% endfor %}
```



### `map`

`inventory.ini`：

```
# cassandra 服务
[cassandra]
10.0.1.20
10.0.1.21
10.0.1.22
```

根据上面的 `[cassandra]` 主机组动态生成 `common_cassandra_contact_points=10.0.1.20:9042,10.0.1.21:9042,10.0.1.22:9042`

```yaml
- name: "配置crond服务的common_cassandra_contact_points"
      lineinfile:
        path: ~/deployer-flash-sale/crond/.env
        regexp: "^common_cassandra_contact_points="
        line: "common_cassandra_contact_points={{ groups['cassandra'] | map('regex_replace', '^(.*)$', '\\1:9042') | list | join(',') }}"
```



### `difference`

从所有主机中去除属于management主机组的主机，生成一个新的主机列表：

- `inventory.ini`：

  ```
  # api 服务，运行 service 服务
  [api]
  10.0.1.31
  10.0.1.32
  10.0.1.33
  10.0.1.34
  
  # openresty 服务
  [openresty]
  10.0.1.40
  
  # 管理主机
  [management]
  # 一般不需要修改，因为配置管理机是本地执行ansible
  127.0.0.1
  ```

- `playbook`：

  ```yaml
  - name: "配置测试节点的操作系统参数"
    hosts: "{{ groups['all'] | difference(groups['management']) }}"
    tasks:
      - name: "修改nofile配置"
        shell: |
          set -e
          sudo grep -q "^\* soft nofile" /etc/security/limits.conf && sudo sed -i '/^\* soft nofile/c \* soft nofile 65535' /etc/security/limits.conf || sudo sed -i '/^# End of file/i \* soft nofile 65535' /etc/security/limits.conf
          sudo grep -q "^\* hard nofile" /etc/security/limits.conf && sudo sed -i '/^\* hard nofile/c \* hard nofile 65535' /etc/security/limits.conf || sudo sed -i '/^# End of file/i \* hard nofile 65535' /etc/security/limits.conf
      
  ```

  - groups['all']：获取Ansible中定义的所有主机的列表。在Ansible中，groups是一个字典，包含了所有定义在inventory文件中的主机组。groups['all']会返回一个包含所有主机的列表。
  - groups['management']：获取名为management的主机组中的主机列表。
  - difference 过滤器：difference 是Jinja2提供的一个过滤器，用于计算两个列表之间的差集。它会返回一个新列表，包含第一个列表中存在但第二个列表中不存在的元素。



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



### `copy`

>[参考官方文档](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/copy_module.html)

复制本地文件到远程。



#### 复制本地目录到远程

```yaml
- name: "复制deployer目录到远程"
  copy:
  	# deployer 后面有 /，表示复制 deployer 目录下的文件到 deployer-flash-sale 目录下，
  	# 如果 deployer 后面没有 /，表示复制 deployer 整个目录到 deployer-flash-sale 目录下（在 deployer-flash-sale 目录下创建 deployer 目录）。
    src: deployer/
    dest: ~/deployer-flash-sale
```



### `replace`

>[参考官方文档](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/replace_module.html)

替换字符串（注意：不是整行替换）。

```yaml
- name: "Modify /etc/selinux/config file disable selinux"
  replace:
    path: /etc/sysconfig/selinux
    regexp: SELINUX=enforcing
    replace: SELINUX=disabled
```



### `lineinfile`

>[参考官方文档](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/lineinfile_module.html)

该模块确保文件中有特定的行，或者使用反向引用的正则表达式替换现有的行。

```yaml
- name: "修改数据库服务的innodb-buffer-pool-size"
  lineinfile:
    path: ~/deployer-flash-sale/db/.env
    regexp: "^innodb_buffer_pool_size="
    line: "innodb_buffer_pool_size={{innodb_buffer_pool_size}}"

```



### `shell`

> 注意：`ad-hoc` 方式不能指定 `shell` 模块参数，否则 `ansible` 没有结果输出，`https://github.com/ansible/ansible/issues/73005`
>
> [参考官方文档](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/shell_module.html)

执行命令

```sh
ansible demoservers -m shell -a "echo `date`; echo 'Sleep for 5 seconds...'; sleep 5; echo `date`"
```



执行 `Docker Compose` 命令启动 `RocketMQ` 服务

```yaml
- name: "启动RocketMQ服务"
  hosts: rocketmq
  tasks:
    - name: "使用Docker Compose启动RocketMQ服务"
      shell: |
        set -e
        cd ~/deployer-flash-sale/rocketmq
        docker compose pull && docker compose up -d
```



### `wait_for`

等待主机第一个 `Cassandra` 主机的 `9042` 端口就绪：

```yaml
- name: "等待Cassandra服务就绪"
  wait_for:
    host: "{{ groups['cassandra'][0] }}"
    port: 9042
    # 指定在开始检查端口之前等待的秒数。当你希望在服务启动后给它一些时间来初始化，而不是立即开始检查端口是否可用时使用。
    delay: 5
    # 指定等待端口可用的最大时间（以秒为单位）。如果在指定的时间内端口没有变为可用状态，任务将失败。用于防止任务无限期地等待，特别是在服务启动失败或配置错误的情况下。
    timeout: 300
  delegate_to: localhost
  run_once: true
```

- `delay: 5` 指定在开始检查端口之前等待的秒数。
- `timeout: 300` 指定等待端口可用的最大时间（以秒为单位）。



### `group`

>[参考官方文档](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/group_module.html)

创建 `dexterleslie` 普通用户组

```yaml
- name: "创建用户组dexterleslie"
  group:
    name: dexterleslie
    state: present
```



### `user`

>[参考官方文档](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/user_module.html)

```yaml
- name: "创建普通用户dexterleslie，用于rdp登录并运行electerm"
  user:
    name: dexterleslie
    shell: /bin/bash
    group: dexterleslie
    state: present
    password: "{{ 'Root@123' | password_hash('sha512') }}"
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

