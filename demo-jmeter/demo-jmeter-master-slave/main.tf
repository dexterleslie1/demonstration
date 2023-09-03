variable "vsphere_password" {
  type        = string
  sensitive   = true
  description = "vCenter密码"
}
variable "vm_ansible_ip" {
  type    = string
  default = "192.168.1.185"
}
variable "vm_jmeter_master_ip" {
  type    = string
  default = "192.168.1.186"
}
variable "vm_jmeter_slave_ips" {
  type    = list(string)
  default = ["192.168.1.187", "192.168.1.188", "192.168.1.189"/*, "192.168.1.190", "192.168.1.191", "192.168.1.192"*/]
}
variable "vm_ansible_name" {
  type    = string
  default = "demo-ansible"
}
variable "vm_jmeter_master_name" {
  type    = string
  default = "demo-jmeter-master"
}
variable "vm_jmeter_slave_name_prefix" {
  type    = string
  default = "demo-jmeter-slave"
}
variable "vsphere_user" {
  type    = string
  default = "administrator@vsphere.local"
}
variable "vsphere_server" {
  type    = string
  default = "192.168.1.51"
}
variable "vsphere_datacenter" {
  type    = string
  default = "Datacenter"
}
variable "vsphere_datastore" {
  type    = string
  default = "datastore1"
}
variable "vsphere_host" {
  type    = string
  default = "192.168.1.49"
}
variable "vsphere_network" {
  type    = string
  default = "VM Network"
}
variable "vsphere_template" {
  type    = string
  default = "my-template-centOS8"
}
variable "vsphere_template_password" {
  type      = string
  default   = "Root@123"
}
variable "ipv4_gateway" {
  type    = string
  default = "192.168.1.1"
}
variable "vm_folder" {
  type    = string
  default = "vm/private"
}

provider "vsphere" {
  user                 = var.vsphere_user
  password             = var.vsphere_password
  vsphere_server       = var.vsphere_server
  allow_unverified_ssl = true
}

data "vsphere_datacenter" "datacenter" {
  name = var.vsphere_datacenter
}

data "vsphere_datastore" "datastore" {
  name          = var.vsphere_datastore
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

data "vsphere_host" "host" {
  name          = var.vsphere_host
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

data "vsphere_network" "network" {
  name          = var.vsphere_network
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

data "vsphere_virtual_machine" "template" {
  name          = var.vsphere_template
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

# 创建ansible主机
resource "vsphere_virtual_machine" "vm_ansible" {
  name             = var.vm_ansible_name
  resource_pool_id = data.vsphere_host.host.resource_pool_id
  datastore_id     = data.vsphere_datastore.datastore.id
  num_cpus         = 4
  memory           = 4096
  guest_id         = data.vsphere_virtual_machine.template.guest_id
  scsi_type        = data.vsphere_virtual_machine.template.scsi_type
  folder           = "/${data.vsphere_datacenter.datacenter.name}/${var.vm_folder}"
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
        host_name = var.vm_ansible_name
        domain    = var.vm_ansible_name
      }
      network_interface {
        ipv4_address = var.vm_ansible_ip
        ipv4_netmask = 24
      }
      ipv4_gateway = var.ipv4_gateway
    }
  }

  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = "root"
      password = var.vsphere_template_password
      host     = self.default_ip_address
    }

    inline = [
      // 安装ansible
      "yum remove -y ansible",
      "yum -y install https://bucketxyh.oss-cn-hongkong.aliyuncs.com/ansible/ansible-2.9.27-1.el8.noarch.rpm https://bucketxyh.oss-cn-hongkong.aliyuncs.com/ansible/sshpass-1.09-4.el8.x86_64.rpm"
    ]
  }
}

# 创建jmeter master vm
resource "vsphere_virtual_machine" "vm_jmeter_master" {
  name             = var.vm_jmeter_master_name
  resource_pool_id = data.vsphere_host.host.resource_pool_id
  datastore_id     = data.vsphere_datastore.datastore.id
  num_cpus         = 8
  memory           = 4096
  guest_id         = data.vsphere_virtual_machine.template.guest_id
  scsi_type        = data.vsphere_virtual_machine.template.scsi_type
  folder           = "/${data.vsphere_datacenter.datacenter.name}/${var.vm_folder}"
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
        host_name = var.vm_jmeter_master_name
        domain    = var.vm_jmeter_master_name
      }
      network_interface {
        ipv4_address = var.vm_jmeter_master_ip
        ipv4_netmask = 24
      }
      ipv4_gateway = var.ipv4_gateway
    }
  }

  depends_on = [resource.vsphere_virtual_machine.vm_ansible]
}

# 创建jmeter slave vm
resource "vsphere_virtual_machine" "vm_jmeter_slave" {
  count            = length(var.vm_jmeter_slave_ips)
  name             = "${var.vm_jmeter_slave_name_prefix}-${count.index}"
  resource_pool_id = data.vsphere_host.host.resource_pool_id
  datastore_id     = data.vsphere_datastore.datastore.id
  num_cpus         = 4
  memory           = 6144
  guest_id         = data.vsphere_virtual_machine.template.guest_id
  scsi_type        = data.vsphere_virtual_machine.template.scsi_type
  folder           = "/${data.vsphere_datacenter.datacenter.name}/${var.vm_folder}"
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
        host_name = "${var.vm_jmeter_slave_name_prefix}-${count.index}"
        domain    = "${var.vm_jmeter_slave_name_prefix}-${count.index}"
      }
      network_interface {
        ipv4_address = var.vm_jmeter_slave_ips[count.index]
        ipv4_netmask = 24
      }
      ipv4_gateway = var.ipv4_gateway
    }
  }

  depends_on = [resource.vsphere_virtual_machine.vm_ansible]
}

# 在所有虚拟机创建成功后执行脚本
resource "null_resource" "init_jmeter_master" {
  triggers = {
    # 因为这个属性每次运行都变化，所以每次执行apply都会运行这个null_resource
    always_run = "${timestamp()}"
  }

  # 保证/usr/local/my-workspace目录存在
  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = "root"
      password = var.vsphere_template_password
      host     = var.vm_ansible_ip
    }

    inline = [
      "mkdir -p /usr/local/my-workspace/playbooks"
    ]
  }

  # 复制playbooks到anisble vm
  provisioner "file" {
    connection {
      type     = "ssh"
      user     = "root"
      password = var.vsphere_template_password
      host     = var.vm_ansible_ip
    }

    source      = "./playbooks/"
    destination = "/usr/local/my-workspace/playbooks/"
  }

  # 配置jmeter master
  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = "root"
      password = var.vsphere_template_password
      host     = var.vm_ansible_ip
    }

    inline = [
      "ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook /usr/local/my-workspace/playbooks/config-jmeter.yml --inventory ${var.vm_jmeter_master_ip}, --user root -e ansible_ssh_pass='${var.vsphere_template_password}' -e varMasterMode=true -e varRemoteHosts=${join(",", var.vm_jmeter_slave_ips)} -e var_heap_mx=4"
    ]
  }

  // 等待资源准备好才执行此资源
  depends_on = [resource.vsphere_virtual_machine.vm_jmeter_master]
}

# 在所有虚拟机创建成功后执行脚本
resource "null_resource" "init_jmeter_slave" {
  count = length(var.vm_jmeter_slave_ips)

  triggers = {
    # 因为这个属性每次运行都变化，所以每次执行apply都会运行这个null_resource
    always_run = "${timestamp()}"
  }

  # 配置jmeter slave
  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = "root"
      password = var.vsphere_template_password
      host     = var.vm_ansible_ip
    }

    inline = [
      "ANSIBLE_HOST_KEY_CHECKING=False ansible-playbook /usr/local/my-workspace/playbooks/config-jmeter.yml --inventory ${var.vm_jmeter_slave_ips[count.index]}, --user root -e ansible_ssh_pass='${var.vsphere_template_password}' -e var_slave_mode=true -e varRmiListenIp=${var.vm_jmeter_slave_ips[count.index]} -e var_heap_mx=4"
    ]
  }

  // 等待资源准备好才执行此资源
  depends_on = [resource.vsphere_virtual_machine.vm_jmeter_slave]
}

