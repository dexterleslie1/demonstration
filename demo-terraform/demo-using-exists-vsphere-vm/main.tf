// 演示使用已经存在的vm
variable "vsphere_password" {
  description = "vsphere password"
  type        = string
  sensitive   = true
}
variable "vsphere_user" {
  type    = string
  default = "administrator@vsphere.local"
}
variable "vsphere_server" {
  type    = string
  default = "192.168.1.51"
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

data "vsphere_virtual_machine" "vm_exists1" {
  // 已经存在的虚拟机名称
  name          = "temp-centOS8.187"
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

// 在已经存在的vm中执行脚本命令
resource "null_resource" "init_vm_exists1" {
  triggers = {
    // 因为这个属性每次运行都变化，所以每次执行apply都会运行这个null_resource
    always_run = "${timestamp()}"
  }
  connection {
    type     = "ssh"
    user     = "root"
    password = "Root@123"
    host     = data.vsphere_virtual_machine.vm_exists1.default_ip_address
  }

  provisioner "remote-exec" {
    inline = [
      "date >> /tmp/1.log",
    ]
  }

  // 等待资源准备好才执行此资源
  depends_on = [data.vsphere_virtual_machine.vm_exists1]
}
