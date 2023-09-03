variable "vsphere_user" {
  type    = string
  default = "administrator@vsphere.local"
}
variable "vsphere_password" {
  type        = string
  sensitive   = true
  description = "vCenter密码"
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
  name          = "datastore1"
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
  firmware                = "efi"
  efi_secure_boot_enabled = true
  network_interface {
    network_id   = data.vsphere_network.network.id
    adapter_type = data.vsphere_virtual_machine.template.network_interface_types[0]
  }
  disk {
    label            = "disk0"
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
        ipv4_address = "192.168.1.10"
        ipv4_netmask = 24
      }
      ipv4_gateway = "192.168.1.1"
    }
  }
}

resource "null_resource" "init_vm" {
  triggers = {
    always_run = "${timestamp()}"
  }

  connection {
    type     = "ssh"
    user     = "root"
    password = "Root@123"
    host     = resource.vsphere_virtual_machine.vm.default_ip_address
  }

  // 保证 /root/my-workspace、/root/my-workspace1/for-testing-no-removing 目录存在
  // NOTE: 尝试过不能使用 ~
  provisioner "remote-exec" {
    inline = [
      "mkdir -p /root/my-workspace",
      "mkdir -p /root/my-workspace1/for-testing-no-removing"
    ]
  }

  // https://developer.hashicorp.com/terraform/language/resources/provisioners/file
  provisioner "file" {
    source      = "../README.md"
    destination = "/root/my-workspace/README.md"
  }

  provisioner "file" {
    source      = "../for-testing-no-removing/"
    destination = "/root/my-workspace1/for-testing-no-removing/"
  }
}
