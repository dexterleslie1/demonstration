variable "vsphere_password" {
  type        = string
  description = "vCenter密码"
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

data "vsphere_virtual_machine" "template" {
  name          = "my-template-centOS8"
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

// ansible主机
resource "vsphere_virtual_machine" "vm_ansible" {
  name             = "demo-terraform-ansible"
  resource_pool_id = data.vsphere_host.host.resource_pool_id
  datastore_id     = data.vsphere_datastore.datastore.id
  num_cpus         = 4
  memory           = 2048
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
        host_name = "demo-terraform-ansible"
        domain    = "demo.terraform.ansible.com"
      }
      network_interface {
        ipv4_address = "192.168.1.150"
        ipv4_netmask = 24
      }
      ipv4_gateway = "192.168.1.1"
    }
  }

  connection {
    type     = "ssh"
    user     = "root"
    password = "Root@123"
    host     = self.default_ip_address
  }

  provisioner "file" {
    source      = "./playbooks"
    destination = "/usr/local/my-workspace"
  }

  provisioner "remote-exec" {
    inline = [
      // 安装ansible
      "yum remove -y ansible",
      "yum -y install https://bucketxyh.oss-cn-hongkong.aliyuncs.com/ansible/ansible-2.9.27-1.el8.noarch.rpm https://bucketxyh.oss-cn-hongkong.aliyuncs.com/ansible/sshpass-1.09-4.el8.x86_64.rpm"
    ]
  }
}

// devops主机，里面运行jenkins、ansible等服务
resource "vsphere_virtual_machine" "vm_devops_master" {
  name             = "demo-terraform-devops-master"
  resource_pool_id = data.vsphere_host.host.resource_pool_id
  datastore_id     = data.vsphere_datastore.datastore.id
  num_cpus         = 4
  memory           = 8192
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
        host_name = "demo-terraform-devops-master"
        domain    = "demo.terraform.devops.master.com"
      }
      network_interface {
        ipv4_address = "192.168.1.151"
        ipv4_netmask = 24
      }
      ipv4_gateway = "192.168.1.1"
    }
  }

  // 连接到ansible主机并执行devops_master自动配置playbook
  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = "root"
      password = "Root@123"
      host     = vsphere_virtual_machine.vm_ansible.default_ip_address
    }

    inline = [
      "ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook /usr/local/my-workspace/config-devops-master.yml --inventory 192.168.1.151, --user root -e ansible_ssh_pass='Root@123'"
    ]
  }

  depends_on = [vsphere_virtual_machine.vm_centos8_slave, vsphere_virtual_machine.vm_centos8_uat]

}

// jenkins centos8-slave主机
resource "vsphere_virtual_machine" "vm_centos8_slave" {
  name             = "demo-terraform-devops-centos8-slave"
  resource_pool_id = data.vsphere_host.host.resource_pool_id
  datastore_id     = data.vsphere_datastore.datastore.id
  num_cpus         = 4
  memory           = 2048
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
        host_name = "demo-terraform-devops-centos8-slave"
        domain    = "demo.terraform.devops.centos8.slave.com"
      }
      network_interface {
        ipv4_address = "192.168.1.152"
        ipv4_netmask = 24
      }
      ipv4_gateway = "192.168.1.1"
    }
  }

  // 连接到ansible主机并执行centos8-slave自动配置playbook
  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = "root"
      password = "Root@123"
      host     = vsphere_virtual_machine.vm_ansible.default_ip_address
    }

    inline = [
      "ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook /usr/local/my-workspace/config-devops-centos8-slave.yml --inventory 192.168.1.152, --user root -e ansible_ssh_pass='Root@123'"
    ]
  }

  depends_on = [vsphere_virtual_machine.vm_ansible]
}

// SIT、UAT主机
resource "vsphere_virtual_machine" "vm_centos8_uat" {
  name             = "demo-terraform-devops-centos8-uat"
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
        host_name = "demo-terraform-devops-centos8-uat"
        domain    = "demo.terraform.devops.centos8.uat.com"
      }
      network_interface {
        ipv4_address = "192.168.1.155"
        ipv4_netmask = 24
      }
      ipv4_gateway = "192.168.1.1"
    }
  }

  // 连接到ansible主机并执行centos8-uat自动配置playbook
  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = "root"
      password = "Root@123"
      host     = vsphere_virtual_machine.vm_ansible.default_ip_address
    }

    inline = [
      "ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook /usr/local/my-workspace/config-devops-centos8-uat.yml --inventory 192.168.1.155, --user root -e ansible_ssh_pass='Root@123'"
    ]
  }

  depends_on = [vsphere_virtual_machine.vm_ansible]
}
