# `providers`用法

## `vsphere provider`用法

> https://registry.terraform.io/providers/hashicorp/vsphere/latest/docs

`vsphere provider`详细用法请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-terraform/vsphere)

```bash
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
  folder           = "private"
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

