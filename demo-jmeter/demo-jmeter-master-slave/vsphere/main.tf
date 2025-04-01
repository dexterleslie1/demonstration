variable "vsphere_password" {
  type        = string
  sensitive   = true
  description = "vCenter密码"
}
variable "ssh_user" {
  type = string
  default = "root"
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
  default = ["192.168.1.187"/*, "192.168.1.188", "192.168.1.189" , "192.168.1.190", "192.168.1.191", "192.168.1.192"*/]
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
  type    = string
  default = "Root@123"
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
  num_cpus         = 2
  memory           = 2048
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

  # 保证/usr/local/my-workspace目录存在
  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = var.ssh_user
      password = var.vsphere_template_password
      host     = self.default_ip_address
    }

    inline = [
      "mkdir -p /usr/local/my-workspace"
    ]
  }

  # 复制demo-jmeter-customize-plugin-1.0.0.jar
  provisioner "file" {
    connection {
      type     = "ssh"
      user     = var.ssh_user
      password = var.vsphere_template_password
      host     = self.default_ip_address
    }

    source      = "../../demo-jmeter-customize-plugin/target/demo-jmeter-customize-plugin-1.0.0.jar"
    destination = "/usr/local/my-workspace/demo-jmeter-customize-plugin-1.0.0.jar"
  }
  # 复制demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar
  provisioner "file" {
    connection {
      type     = "ssh"
      user     = var.ssh_user
      password = var.vsphere_template_password
      host     = self.default_ip_address
    }

    source      = "../../demo-jmeter-customize-plugin/target/demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar"
    destination = "/usr/local/my-workspace/demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar"
  }
  # 复制jmeter.jmx
  provisioner "file" {
    connection {
      type     = "ssh"
      user     = var.ssh_user
      password = var.vsphere_template_password
      host     = self.default_ip_address
    }

    source      = "../../demo-jmeter-customize-plugin/jmeter.jmx"
    destination = "/usr/local/my-workspace/jmeter.jmx"
  }

  # 复制playbooks到anisble vm
  provisioner "file" {
    connection {
      type     = "ssh"
      user     = var.ssh_user
      password = var.vsphere_template_password
      host     = self.default_ip_address
    }

    source      = "../setup.sh"
    destination = "/usr/local/my-workspace/setup.sh"
  }

  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = var.ssh_user
      password = var.vsphere_template_password
      host     = self.default_ip_address
    }

    inline = [
      "sh /usr/local/my-workspace/setup.sh",
      "sudo yum install nc -y",
      "sudo yum install sshpass -y",
    ]
  }
}

# 创建jmeter master vm
resource "vsphere_virtual_machine" "vm_jmeter_master" {
  name               = var.vm_jmeter_master_name
  resource_pool_id   = data.vsphere_host.host.resource_pool_id
  datastore_id       = data.vsphere_datastore.datastore.id
  num_cpus           = 8
  memory             = 6144
  cpu_limit          = (8 * 2200)
  cpu_reservation    = (8 * 2200)
  cpu_share_level    = "custom"
  cpu_share_count    = (8 * 2200)
  memory_limit       = 6144
  memory_reservation = 6144
  memory_share_level = "custom"
  memory_share_count = 122880
  guest_id           = data.vsphere_virtual_machine.template.guest_id
  scsi_type          = data.vsphere_virtual_machine.template.scsi_type
  folder             = "/${data.vsphere_datacenter.datacenter.name}/${var.vm_folder}"
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
  count              = length(var.vm_jmeter_slave_ips)
  name               = "${var.vm_jmeter_slave_name_prefix}-${count.index}"
  resource_pool_id   = data.vsphere_host.host.resource_pool_id
  datastore_id       = data.vsphere_datastore.datastore.id
  num_cpus           = 4
  memory             = 6144
  cpu_limit          = (4 * 2200)
  cpu_reservation    = (4 * 2200)
  cpu_share_level    = "custom"
  cpu_share_count    = (4 * 2200)
  memory_limit       = 6144
  memory_reservation = 6144
  memory_share_level = "custom"
  memory_share_count = 122880
  guest_id           = data.vsphere_virtual_machine.template.guest_id
  scsi_type          = data.vsphere_virtual_machine.template.scsi_type
  folder             = "/${data.vsphere_datacenter.datacenter.name}/${var.vm_folder}"
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

  depends_on = [resource.null_resource.init_jmeter_master]
}

# 在所有虚拟机创建成功后执行脚本
resource "null_resource" "init_jmeter_master" {
  triggers = {
    # 因为这个属性每次运行都变化，所以每次执行apply都会运行这个null_resource
    always_run = "${timestamp()}"
  }

  # 等待实例ssh ready
  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = var.ssh_user
      password = var.vsphere_template_password
      host     = var.vm_ansible_ip
    }

    inline = [
      "while ! nc -w 5 -z ${var.vm_jmeter_master_ip} 22; do echo 'retry until ${var.vm_jmeter_master_ip} ssh ready ...'; sleep 5; done",
      "echo '${var.vm_jmeter_master_ip} ssh is ready!'"
    ]
  }

  # 配置jmeter master
  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = var.ssh_user
      password = var.vsphere_template_password
      host     = var.vm_ansible_ip
    }

    inline = [
      "dcli jmeter install --install=y --target_host=${var.vm_jmeter_master_ip} --target_host_password=${var.vsphere_template_password} --mode=master --remote_hosts=${join(",", var.vm_jmeter_slave_ips)} -xmx=4",
      # 复制demo-jmeter-customize-plugin-1.0.0.jar
      # 复制demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar
      # https://serverfault.com/questions/330503/scp-without-known-hosts-check
      "sudo sshpass -p '${var.vsphere_template_password}' scp -o StrictHostKeyChecking=no /usr/local/my-workspace/demo-jmeter-customize-plugin-1.0.0.jar ${var.ssh_user}@${var.vm_jmeter_master_ip}:demo-jmeter-customize-plugin-1.0.0.jar",
      "sudo sshpass -p '${var.vsphere_template_password}' scp -o StrictHostKeyChecking=no /usr/local/my-workspace/demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar ${var.ssh_user}@${var.vm_jmeter_master_ip}:demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar",
      "sudo sshpass -p '${var.vsphere_template_password}' ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${var.vm_jmeter_master_ip} sudo mv demo-jmeter-customize-plugin-1.0.0.jar /usr/local/software/jmeter/lib/ext/demo-jmeter-customize-plugin-1.0.0.jar",
      "sudo sshpass -p '${var.vsphere_template_password}' ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${var.vm_jmeter_master_ip} sudo mv demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar /usr/local/software/jmeter/lib/ext/demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar",
      "sudo sshpass -p '${var.vsphere_template_password}' ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${var.vm_jmeter_master_ip} sudo chown root:root /usr/local/software/jmeter/lib/ext/demo-jmeter*.jar",
      # 复制jmeter.jmx
      "sudo sshpass -p '${var.vsphere_template_password}' scp -o StrictHostKeyChecking=no /usr/local/my-workspace/jmeter.jmx ${var.ssh_user}@${var.vm_jmeter_master_ip}:jmeter.jmx",
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
      user     = var.ssh_user
      password = var.vsphere_template_password
      host     = var.vm_ansible_ip
    }

    inline = [
      "dcli jmeter install --install=y --target_host=${var.vm_jmeter_slave_ips[count.index]} --target_host_password=${var.vsphere_template_password} --mode=slave --slave_listen_ip=${var.vm_jmeter_slave_ips[count.index]} -xmx=4",
      # 复制demo-jmeter-customize-plugin-1.0.0.jar
      # 复制demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar
      "sudo sshpass -p '${var.vsphere_template_password}' scp -o StrictHostKeyChecking=no /usr/local/my-workspace/demo-jmeter-customize-plugin-1.0.0.jar ${var.ssh_user}@${var.vm_jmeter_slave_ips[count.index]}:demo-jmeter-customize-plugin-1.0.0.jar",
      "sudo sshpass -p '${var.vsphere_template_password}' scp -o StrictHostKeyChecking=no /usr/local/my-workspace/demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar ${var.ssh_user}@${var.vm_jmeter_slave_ips[count.index]}:demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar",
      "sudo sshpass -p '${var.vsphere_template_password}' ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${var.vm_jmeter_slave_ips[count.index]} sudo mv demo-jmeter-customize-plugin-1.0.0.jar /usr/local/software/jmeter/lib/ext/demo-jmeter-customize-plugin-1.0.0.jar",
      "sudo sshpass -p '${var.vsphere_template_password}' ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${var.vm_jmeter_slave_ips[count.index]} sudo mv demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar /usr/local/software/jmeter/lib/ext/demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar",
      "sudo sshpass -p '${var.vsphere_template_password}' ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${var.vm_jmeter_slave_ips[count.index]} sudo chown root:root /usr/local/software/jmeter/lib/ext/demo-jmeter*.jar",
      # 重新启动jmeter slave否则因为没有加载插件报错
      "sudo sshpass -p '${var.vsphere_template_password}' ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${var.vm_jmeter_slave_ips[count.index]} sudo systemctl restart jmeter-server",
    ]
  }

  // 等待资源准备好才执行此资源
  depends_on = [resource.vsphere_virtual_machine.vm_jmeter_slave]
}

