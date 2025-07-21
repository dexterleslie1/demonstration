# `Ansible`



## `Puppet`、`Chef`、`SaltStack`、`Ansible` 怎么选择？

大型互联网公司对配置管理工具的选择通常取决于**业务场景、技术栈适配性、团队经验**和**工具特性**（如性能、扩展性、学习成本）。以下是主流工具（Puppet、Chef、SaltStack、Ansible）在实际中的使用情况分析，以及典型公司的选择倾向：


### **一、工具核心特性对比**
| 工具          | 语言/架构       | 通信方式        | 执行模式      | 学习成本 | 典型场景                     |
| ------------- | --------------- | --------------- | ------------- | -------- | ---------------------------- |
| **Puppet**    | Ruby（DSL）     | C/S（Agent）    | 推送/拉取     | 高       | 大规模静态基础设施管理       |
| **Chef**      | Ruby（DSL）     | C/S（Agent）    | 拉取          | 中高     | 灵活的自定义配置流程         |
| **SaltStack** | Python（PyDSL） | C/S（ZeroMQ）   | 推送/实时事件 | 中       | 高性能、实时控制的大规模集群 |
| **Ansible**   | Python（YAML）  | Push（SSH/API） | 推送          | 低       | 快速部署、混合云/容器化      |


### **二、大型互联网公司的选择实践**
#### **1. Ansible：近年最流行的“轻量高效”选择**
Ansible 凭借 **无 Agent、YAML 剧本、简单语法** 特性，成为近年大型互联网公司的“新宠”，尤其适合需要快速上手、跨环境（物理机/虚拟机/云主机/容器）协作的场景。  

**典型用户**：  
- **GitHub**：早期用 Puppet 迁移至 Ansible，因“减少维护成本”和“更简洁的配置表达”。  
- **Red Hat**（Ansible 母公司）：自身大规模基础设施管理。  
- **阿里云、腾讯云**：部分业务线（如边缘计算节点、容器编排辅助配置）使用 Ansible 简化部署。  
- **字节跳动**：在混合云场景中，Ansible 用于跨公有云（AWS/Azure）和私有云的资源编排。  

**优势**：  
- 无 Agent 部署，降低被管理节点的资源消耗（尤其适合容器化环境）。  
- YAML 剧本可读性强，非运维人员（如开发）也能参与编写。  
- 生态丰富（Ansible Galaxy 提供数万个预定义角色），支持与 Kubernetes、Terraform 等工具集成。  


#### **2. SaltStack：高性能集群的“实时控制”首选**
SaltStack 基于 **事件驱动架构** 和 **ZeroMQ 高性能通信**，适合需要**大规模并行执行、实时响应**的场景（如百万级服务器集群的批量操作、故障快速修复）。  

**典型用户**：  
- **AWS（部分场景）**：早期用 Chef 迁移至 SaltStack，用于 EC2 实例的实时配置管理。  
- **Netflix**：在微服务架构中，SaltStack 用于服务实例的快速扩缩容和配置同步。  
- **华为云、新华三**：企业级云平台中，SaltStack 用于底层基础设施的批量运维。  

**优势**：  
- 支持“主-从”（Master-Minion）架构下的毫秒级批量操作（如同时修改 10 万台服务器的时区）。  
- 内置状态系统（State）和反应器（Reactor），可实现“事件触发-自动修复”的闭环（如检测到服务宕机时自动重启）。  


#### **3. Puppet/Chef：传统大型企业的“稳定之选”**
Puppet 和 Chef 是更早期的配置管理工具，适合**技术栈成熟、需要长期稳定维护**的企业（如金融、电信行业的大型互联网业务）。  

**典型用户**：  
- **Google（早期）**：曾大规模使用 Puppet 管理数据中心基础设施（后逐步自研工具替代）。  
- **摩根大通（金融行业）**：核心交易系统的服务器配置管理（需严格合规性和审计）。  
- **传统企业数字化转型项目**（如电信运营商的 OSS/BSS 系统）：因团队熟悉 Ruby 技术栈，仍沿用 Chef。  

**劣势**：  
- 学习曲线陡峭（需掌握 Ruby DSL 和复杂的模块开发）。  
- 维护成本高（自定义模块需频繁适配新版本工具）。  


### **三、选择决策的关键因素**
大型互联网公司选择配置管理工具时，通常会综合考虑以下因素：  

#### **1. 规模与性能**  
- 百万级服务器集群：优先选 SaltStack（高性能通信）或自研工具（如 Google 的 Borgcfg）。  
- 千台~万台级集群：Ansible（无 Agent 更轻量）或 Puppet（稳定生态）。  


#### **2. 技术栈适配性**  
- 已有 Ruby 团队：Puppet/Chef（需接受较高的学习成本）。  
- Python 技术栈主导：SaltStack/Ansible（更易集成现有工具链）。  


#### **3. 场景需求**  
- **静态基础设施**（如传统机房服务器）：Puppet（声明式配置更稳定）。  
- **动态/弹性环境**（如 Kubernetes 集群、云原生应用）：Ansible（与 Helm、Kustomize 等工具互补）。  
- **实时控制**（如故障自动修复、紧急补丁推送）：SaltStack（事件驱动+实时通信）。  


#### **4. 团队经验与生态**  
- 团队熟悉 YAML：Ansible（快速上手）。  
- 需要丰富的第三方模块：Puppet（Puppet Forge 有超 6000 个模块）。  


### **四、总结**
- **Ansible** 是当前大型互联网公司的“主流选择”，尤其在混合云、容器化和快速迭代场景中。  
- **SaltStack** 在需要高性能、实时控制的大规模集群中仍有不可替代性（如 AWS 部分核心服务）。  
- **Puppet/Chef** 主要用于传统企业或技术栈成熟的场景（如金融行业），但新业务已逐渐转向更轻量的工具。  

**趋势**：随着云原生（Kubernetes）和 IaC（基础设施即代码）的普及，配置管理工具正与 Terraform、Helm 等工具融合，形成“声明式配置+自动化执行”的完整链路，而 Ansible 因其与这些工具的良好兼容性，未来仍可能保持增长。



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



判断变量真假：

- `inventory.ini` 中定义变量

  ```ini
  # zookeeper、redis、prometheus 等公共服务
  [common]
  192.168.1.198
  
  # api 服务，运行 service 服务
  [api]
  192.168.1.170
  192.168.1.186
  192.168.1.187
  192.168.1.188
  
  # openresty 服务
  [openresty]
  192.168.1.185
  
  # 管理主机
  [management]
  # 一般不需要修改，因为配置管理机是本地执行ansible
  192.168.1.196
  
  [api:vars]
  # redis 是否集群模式
  redisClusterMode=True
  ```

- `play book` 中判断变量的真假

  ```yaml
  - name: "配置api服务的deployer"
    hosts: api
    tasks:
      - name: "配置api服务的redisClusterNodes"
        lineinfile:
          path: ~/deployer-demo-jedis-benchmark/service/.env
          regexp: "^redisClusterNodes="
          line: "redisClusterNodes={{ groups['common'][0] }}:6380,{{ groups['common'][0] }}:6381,{{ groups['common'][0] }}:6382"
        when: redisClusterMode
      - name: "配置api服务的redisStandaloneHost"
        lineinfile:
          path: ~/deployer-demo-jedis-benchmark/service/.env
          regexp: "^redisStandaloneHost="
          line: "redisStandaloneHost={{ groups['common'][0] }}"
        when: not redisClusterMode
  
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



添加用户到 `suoders` 列表中：

```yaml
- name: "将dexterleslie添加到sudoers"
  lineinfile:
    # sudoers文件路径
    path: /etc/sudoers
    line: "dexterleslie ALL=(ALL) ALL"  # 允许用户执行所有sudo命令
    # 可选：允许无密码sudo（根据需求调整）
    # line: "dexterleslie ALL=(ALL) NOPASSWD:ALL"
    # 用visudo验证语法，避免错误
    validate: 'visudo -cf %s'
    # 确保该行存在
    state: present
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

