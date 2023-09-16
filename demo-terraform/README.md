## terraform用法



### 安装terraform

#### centOS8安装terraform

> https://developer.hashicorp.com/terraform/tutorials/docker-get-started/install-cli

```
# 安装yum-utils
sudo yum install -y yum-utils

# 新增HashiCorp仓库
sudo yum-config-manager --add-repo https://rpm.releases.hashicorp.com/RHEL/hashicorp.repo

# 安装terraform，NOTE: 1.3.9之后的plugin_cache_dir配置不起作用
sudo yum -y install terraform-1.3.9

# 配置terraform provider本地全局缓存，否则每次terraform init都会在当前目录重新下载provider导致terrafrom init慢
# https://developer.hashicorp.com/terraform/cli/config/config-file

# 创建目录 ~/.terraform.d/plugin-cache
mkdir -p ~/.terraform.d/plugin-cache

# 创建 ~/.terraformrc文件内容如下
plugin_cache_dir = "$HOME/.terraform.d/plugin-cache"
disable_checkpoint = true

# 使用dcli安装docker环境

# 使用docker测试terraform是否安装成功

# 创建main.tf文件
terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0.1"
    }
  }
}

provider "docker" {}

resource "docker_image" "nginx" {
  name         = "nginx"
  keep_locally = false
}

resource "docker_container" "nginx" {
  image = docker_image.nginx.image_id
  name  = "tutorial"

  ports {
    internal = 80
    external = 8000
  }
}

# 初始项目，下载provider插件
terraform init
# 调试模式执行init
TF_LOG=trace terraform init

# 执行main.tf文件
terraform apply

# 打开浏览器访问 http://localhost:80000

# 取消main.tf执行
terraform destroy
```



#### ubuntu安装terraform

```
# 使用snap安装
sudo snap install terraform --classic
```



### terraform基础



#### expression表达式



##### for表达式

> 参考 demo-expression-for.tf



#### local变量

> 参考 demo-variable.tf#演示本地变量



#### 使用variable.validation验证参数

> 参考 demo-variable.tf#参数验证



#### 使用terraform.tfvars提供变量值

```
# 在terraform工作目录中创建名为terraform.tfvars的文件，内容如下:
my_var1 = "Hello world!"
```



#### module

> NOTE: 没有需求用到，暂时不需要研究。





### workspace

> 参考 demo-workspace
>
> https://developer.hashicorp.com/terraform/language/state/workspaces
> https://developer.hashicorp.com/terraform/cli/workspaces



```
# 查询workspace列表
terraform workspace list

# 新建workspace
terraform workspace new prod
terrafrom workspace new dev

# 切换workspace
terraform workspace select default
terraform workspace select prod

# 删除workspace
terraform workspace delete prod

# 切换workspace后指定tfvars文件
terraform workspace select prod
terraform apply -var-file=prod.tfvars
```



### terraform cli用法



#### terraform apply

```
# 执行main.tf文件
terraform apply

# terraform从v0.15.2起弃用terraform taint，使用terraform apply -replace替换
# https://jeffbrown.tech/terraform-taint-replace/
terraform apply -replace="google_compute_instance.demo_jmeter_master_vm"
terraform apply -replace="google_compute_instance.demo_jmeter_slave_vm"

# 自动确认
terraform apply --auto-approve
```



#### terraform plan

> 查看terraform将要创建资源的参数但不实际执行资源创建

```
# 执行命令
terraform plan
```



#### terraform show

> 把tf文件中所有变量解析并显示实际值

```
# 执行命令
terraform show
```



#### terraform refresh

> 此命令读取远程资源状态并同步到本地状态存储中。用于处理配置漂移情况，执行refresh后再执行terraform show就能够查看远程状态已经成功同步到本地状态存储中。



#### terraform console

> https://developer.hashicorp.com/terraform/cli/commands/console
> https://stackoverflow.com/questions/65818997/how-can-i-print-debug-all-available-fields-of-a-data-source-resource
>
> 使用terraform console调试 

```
# main.tf内容如下:
variable "vsphere_user" {
  type    = string
  default = "xxx@vsphere.local"
}
variable "vsphere_password" {
  type = string
  default = "xxx"
}
variable "vsphere_server" {
  type = string
  default = "192.168.1.xxx"
}

provider "vsphere" {
  user                 = var.vsphere_user
  password             = var.vsphere_password
  vsphere_server       = var.vsphere_server
  allow_unverified_ssl = true
}

data "vsphere_datacenter" "datacenter" {
  name = "Datacenter"
}

data "vsphere_datastore" "datastore" {
  name          = "datastoreraid0"
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

data "vsphere_host" "host" {
  name          = "192.168.1.49"
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

data "vsphere_network" "network" {
  name          = "VM Network"
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

data "vsphere_virtual_machine" "template" {
  name          = "my-template-centOS8"
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

# 初始化项目
terraform init

# 通过apply同步远程状态到本地
terraform apply

# 进入terraform console
terraform console
# 在console中显示变量值
> var.vsphere_user
# 在console中打印datasource template的值
> data.vsphere_virtual_machine.template

```



#### terraform destroy删除指定的资源

> https://www.devopsschool.com/blog/how-to-destroy-one-specific-resource-from-tf-file-in-terraform/

```
# 显示所有资源
terraform state list

# 删除指定的资源
terraform destroy --target=vsphere_virtual_machine.vm_devops_master
```



#### terraform output

> 从state文件中读取output变量并打印
>
> https://developer.hashicorp.com/terraform/cli/commands/output

```
# main.tf内容如下
output "current_dir" {
    value = "${path.module}"
}

terraform init
terraform apply

# 打印变量
terraform output
```



#### terraform state

> https://developer.hashicorp.com/terraform/cli/commands/state/show

```
# 显示指定资源的状态
terraform state show "aws_instance.demo_vm1"
```





### terraform插件本地缓存配置

> 避免每次terraform init时都到官方下载插件
> https://bbs.huaweicloud.com/blogs/352925

```
# 创建目录 ~/.terraform.d/plugin-cache
mkdir ~/.terraform.d/plugin-cache

# 创建目录 /root/.terraform.d/plugin-filesystem-mirror
#mkdir /root/.terraform.d/plugin-filesystem-mirror

# 创建 ~/.terraformrc文件内容如下
plugin_cache_dir = "$HOME/.terraform.d/plugin-cache"
disable_checkpoint = true

#provider_installation {
#  filesystem_mirror {
#    path    = "/root/.terraform.d/plugin-filesystem-mirror"
#    include = ["registry.terraform.io/*/*"]
#  }
#  direct {
#    include = ["registry.terraform.io/*/*"]
#  }
#}

# 切换到main.tf所在目录
# terraform会根据当前main.tf下载插件到 /root/.terraform.d/plugin-filesystem-mirror 目录，以便下次terraform init时使用本地缓存不需要到官方下载插件
#terraform providers mirror ~/.terraform.d/plugin-filesystem-mirror/

# 使用本地插件缓存初始化当前tf目录
terraform init
```



### terraform vsphere provider用法

> https://registry.terraform.io/providers/hashicorp/vsphere/latest/docs

```
# main.tf如下:
variable "vsphere_user" {
  type    = string
  default = "xxx@vsphere.local"
}
variable "vsphere_password" {
  type = string
  default = "xxx"
}
variable "vsphere_server" {
  type = string
  default = "192.168.1.xxx"
}

provider "vsphere" {
  user                 = var.vsphere_user
  password             = var.vsphere_password
  vsphere_server       = var.vsphere_server
  allow_unverified_ssl = true
}

data "vsphere_datacenter" "datacenter" {
  name = "Datacenter"
}

data "vsphere_datastore" "datastore" {
  name          = "datastoreraid0"
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

data "vsphere_host" "host" {
  name          = "192.168.1.49"
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

data "vsphere_network" "network" {
  name          = "VM Network"
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

data "vsphere_virtual_machine" "template" {
  name          = "my-template-centOS8"
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

resource "vsphere_virtual_machine" "vm" {
  name             = "demo-terraform-centOS8"
  resource_pool_id = data.vsphere_host.host.resource_pool_id
  datastore_id     = data.vsphere_datastore.datastore.id
  num_cpus         = 4
  memory           = 4096
  guest_id         = data.vsphere_virtual_machine.template.guest_id
  scsi_type        = data.vsphere_virtual_machine.template.scsi_type
  folder           = "/${data.vsphere_datacenter.datacenter.name}/vm/private"
  # 必须设置efi和secure_boot，否则无法引导系统
  firmware         = "efi"
  efi_secure_boot_enabled = true
  network_interface {
    network_id   = data.vsphere_network.network.id
    adapter_type = data.vsphere_virtual_machine.template.network_interface_types[0]
  }
  disk {
    label = "disk0"
    size             = data.vsphere_virtual_machine.template.disks.0.size
    thin_provisioned = data.vsphere_virtual_machine.template.disks.0.thin_provisioned
  }
  clone {
    template_uuid = data.vsphere_virtual_machine.template.id
    customize {
      # linux系统必须提供此配置
      linux_options {
        host_name = "demo-terraform-centOS8"
        domain    = "demo.terraform.com"
      }
      network_interface {
        ipv4_address = "192.168.1.186"
        ipv4_netmask = 24
      }
      ipv4_gateway = "192.168.1.1"
    }
  }
}
// 在虚拟机创建成功后执行脚本
resource "null_resource" "init_centOS8" {
        triggers = {
                // 因为这个属性每次运行都变化，所以每次执行apply都会运行这个null_resource
                always_run = "${timestamp()}"
        }
        connection {
                type     = "ssh"
                user     = "root"
                password = "xxx"
                host     = "192.168.1.186"
        }

        provisioner "remote-exec" {
                inline = [
                        "date >> /tmp/1.log",
                ]
        }

        // 等待资源 vsphere_virtual_machine.vm 准备好才执行此资源
        depends_on = [vsphere_virtual_machine.vm]
}

# 下载provider
terraform init

# 创建虚拟机
terraform apply

# 删除虚拟机
terraform destroy
```



### Provisioner Connection用法

> https://developer.hashicorp.com/terraform/language/resources/provisioners/connection
> 用于配置SSH或者WinRM连接信息

```
# 在resource中配置connection，命令在虚拟机创建后执行一次
resource "vsphere_virtual_machine" "vm" {

  connection {
    type     = "ssh"
    user     = "root"
    password = "xxx"
    # 通过vsphere_virtual_machine.vm.default_ip_address获取当前虚拟机ip地址
    # https://stackoverflow.com/questions/62498591/how-do-i-get-the-ip-of-a-vsphere-virtual-machine-to-run-ansible-playbook
    host     = self.default_ip_address
  }

  provisioner "remote-exec" {
    inline = [
      # 命令只会在虚拟机被创建后执行一次
      "date >> /tmp/1.log",
    ]
  }
...
}
```



### Provisioner local-exec用法

> https://developer.hashicorp.com/terraform/language/resources/provisioners/local-exec
> NOTE: 在本地(terraform主机)执行命令

```
# main.tf内容如下:
resource "null_resource" "test" {
        triggers = {
                // 因为这个属性每次运行都变化，所以每次执行apply都会运行这个null_resource
                always_run = "${timestamp()}"
        }
        provisioner "local-exec" {
                command = "date"
        }
}

# 初始化
terraform init

# 执行
terraform apply

# 会看到在本地执行命令date的输出
```



### Provisioner remote-exec用法

> https://developer.hashicorp.com/terraform/language/resources/provisioners/remote-exec#scripts
> 用于连接到远程主机执行命令

```
# 在resource中配置remote-exec，命令在虚拟机创建后执行一次
resource "vsphere_virtual_machine" "vm" {

  connection {
    type     = "ssh"
    user     = "root"
    password = "xxx"
    # 通过vsphere_virtual_machine.vm.default_ip_address获取当前虚拟机ip地址
    host     = self.default_ip_address
  }

  provisioner "remote-exec" {
    inline = [
      # 命令只会在虚拟机被创建后执行一次
      "date >> /tmp/1.log",
    ]
  }
...
}
```



### providers



#### local provider

> 参考demo-local-provider.tf



#### random provider

> 参考 demo-provider-random.tf



#### null provider

> 表示没有实际对应resource
>
> https://registry.terraform.io/providers/hashicorp/null/latest/docs

```
# main.tf内容如下
resource "null_resource" "x2" {
  count = 3
  triggers = {
    // 因为这个属性每次运行都变化，所以每次执行apply都会运行这个null_resource
    always_run = "${timestamp()}"
  }
  provisioner "local-exec" {
    command = "echo ++++++++++++ ${count.index}"
  }
}

# 或者另外一个演示main.tf内容如下:
resource "null_resource" "test" {
        triggers = {
                // 因为这个属性每次运行都变化，所以每次执行apply都会运行这个null_resource
                always_run = "${timestamp()}"
        }
        provisioner "local-exec" {
                command = "date"
        }
}

# 初始化
terraform init

# 执行
terraform apply

# 会看到在本地执行命令date的输出
```





#### azure provider

> https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs



##### 配置认证信息

```
# 这里选择Service Principal with a client secret方式配置认证
# https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs/guides/service_principal_client_secret
# NOTE: 根据文档步骤配置client secret，在Add role assignment时Members选择Azure Active Directory中创建的App Application，否则在执行terraform apply时会报告权限不足错误。
```



##### 演示案例

> 参考 azure/main.tf 演示使用azurerm provider部署一个无服务应用



#### aws provider



##### 配置terraform认证信息

> https://registry.terraform.io/providers/hashicorp/aws/latest/docs

```
# 在aws console中创建IAM帐号并记录access_key_id和secret_access_key

# 创建文件 ~/.aws/config，内容如下:
[default]
aws_access_key_id=xxx
aws_secret_access_key=xxx
```



##### aws_vpc资源

> 参考 aws/aws-vpc
>
> https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/vpc.html



##### aws_ami资源

> 参考 aws/aws-ami
>
> https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/ami
>
> 官方发布ami列表
> https://www.centos.org/download/aws-images/#centos-amazon-ami-images



##### aws instance资源

> 参考 aws/aws-instance
>
> https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/instance#vpc_security_group_ids



#### google provider



##### google region和zone列表

> https://cloud.google.com/compute/docs/regions-zones



##### socks5和鉴权环境配置

```
# 配置socks5代理
# https://stackoverflow.com/questions/65939427/how-to-set-up-terraform-behind-proxy
export HTTP_PROXY=socks5://192.168.1.55:1080
export HTTPS_PROXY=socks5://192.168.1.55:1080

# 参考配置provider鉴权
# https://registry.terraform.io/providers/hashicorp/google/latest/docs/guides/provider_reference
# 通过google service account控制台在指定的service account下创建key file
# https://console.cloud.google.com/apis/credentials/serviceaccountkey
# https://cloud.google.com/docs/authentication/application-default-credentials#personal
# 在目录中配置service account json文件
~/.config/gcloud/application_default_credentials.json
```



##### google_compute_instance资源

> 参考gcp/main.tf
>
> https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/compute_instance#nested_access_config



#### ucloud provider

> NOTE: 虚拟机一但创建就预收一个小时费用，贵！暂时不使用。
>
> https://registry.terraform.io/providers/ucloud/ucloud/latest/docs



### provisioners



#### provisioner file

> 参考demo-provisioner-file
>
> https://developer.hashicorp.com/terraform/language/resources/provisioners/file

```
# 初始化项目
terraform init

# 运行
terraform apply

# 删除
terraform destroy
```



### functions函数



#### file函数

> 参考demo-function-file.tf
>
> https://developer.hashicorp.com/terraform/language/functions/file



#### length函数

> 参考demo-function-length.tf
>
> https://developer.hashicorp.com/terraform/language/functions/length



#### templatefile函数

> 参考 demo-function-templatefile.tf



### 后端backends状态存储配置

> https://developer.hashicorp.com/terraform/language/settings/backends/configuration



#### local backend

> NOTE: 通过破坏测试，如果本地丢失terraform相关状态文件，terraform无法知道远程服务的resource状态导致terraform plan会提示创建新的资源。
>
> https://developer.hashicorp.com/terraform/language/settings/backends/local



#### s3 backend

> 参考 demo-backend/s3
>
> NOTE: s3 backend在使用terraform init时就自动到aws s3同步状态。
>
> https://developer.hashicorp.com/terraform/language/settings/backends/s3

```
# 演示步骤
# 进入create-s3目录
terraform init
terraform apply
# 复制output相关信息到use-s3-as-backend/main.tf backend "s3"中

# 进入use-s3-as-backend目录
terraform init
terraform apply

# 登录aws console查看s3 bucket: demo-aws-s3-bucket1中会有一个名为 my-s3-key 的文件，这个文件存储了terraform的状态。

```



#### 使用terraform cloud作为backend

> terraform cloud注册
> https://developer.hashicorp.com/terraform/tutorials/cloud-get-started/cloud-sign-up
>
> 配置terraform cloud
> https://developer.hashicorp.com/terraform/cli/cloud/settings

```
# 步骤如下

# 1、注册一个terraform cloud帐号
# 2、使用terraform login在本地创建一个token用于backend和cloud通讯并存储状态
# 2、登录cloud并创建一个org，NOTE: 不需要手动创建workspace
# 3、复制如下内容到main.tf
terraform {
  cloud {
    organization = "future-demo-my-org"

    workspaces {
      # 设置拥有指定tag的workspace，可以使用terraform workspace select命令切换workspace
      tags = ["my-tag"]
    }
  }
}
# 4、terraform init，根据提示输入一个新的workspace名称: dev
# 5、terraform apply -auto-approve
# 6、创建另外一个workspace: prod
terraform workspace new prod
terraform apply -auto-approve
```

