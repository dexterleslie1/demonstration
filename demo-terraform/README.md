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



## terraform插件本地缓存配置

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



## terraform vsphere provider用法

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

# 下载provider
terraform init

# 创建虚拟机
terraform apply

# 删除虚拟机
terraform destroy
```

