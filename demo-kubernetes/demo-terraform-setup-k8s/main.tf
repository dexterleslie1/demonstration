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
data "vsphere_virtual_machine" "template_centos7" {
  name          = var.vsphere_template_centos7
  datacenter_id = data.vsphere_datacenter.datacenter.id
}

# 创建 demo-k8s-ansible 主机
resource "vsphere_virtual_machine" "demo_ansible" {
  name             = "demo-k8s-ansible"
  resource_pool_id = data.vsphere_host.host.resource_pool_id
  datastore_id     = data.vsphere_datastore.datastore.id
  num_cpus         = 4
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
        host_name = "demo-k8s-ansible"
        domain    = "demo-k8s-ansible"
      }
      network_interface {
        ipv4_address = var.demo_ansible_ip
        ipv4_netmask = 24
      }
      ipv4_gateway = var.ipv4_gateway
    }
  }

}

resource "null_resource" "init_ansible" {
  triggers = {
    always_run = timestamp()
  }
  connection {
    type     = "ssh"
    user     = var.ssh_user
    password = var.ssh_password
    host     = vsphere_virtual_machine.demo_ansible.default_ip_address
  }

  provisioner "file" {
    source      = "./dcli_setup.sh"
    destination = "dcli_setup.sh"
  }

  provisioner "remote-exec" {

    inline = [
      "sudo yum install nc -y",
      "sudo yum install sshpass -y",
      "sudo which dcli &>/dev/null || sudo sh dcli_setup.sh"
    ]
  }
}

# 创建 demo-k8s-master
resource "vsphere_virtual_machine" "demo_master" {
  name             = "demo-k8s-master"
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
        host_name = "demo-k8s-master"
        domain    = "demo-k8s-master"
      }
      network_interface {
        ipv4_address = var.demo_master_ip
        ipv4_netmask = 24
      }
      ipv4_gateway = var.ipv4_gateway
    }
  }

  depends_on = [null_resource.init_ansible]
}

resource "null_resource" "init_master" {
  depends_on = [vsphere_virtual_machine.demo_master]

  triggers = {
    always_run = timestamp()
  }

  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = var.ssh_user
      password = var.ssh_password
      host     = vsphere_virtual_machine.demo_ansible.default_ip_address
    }

    inline = [
      "while ! nc -w 5 -z ${var.demo_master_ip} 22; do echo 'retry until ${var.demo_master_ip} ssh ready ...'; sleep 5; done",
      "dcli k8s install --install=y --target_host=${var.demo_master_ip} --target_host_password='${var.ssh_password}' --mode=master --hostip=${var.demo_master_ip} --hostname=${vsphere_virtual_machine.demo_master.clone[0].customize[0].linux_options[0].host_name}"
    ]
  }
}

# 创建 demo-k8s-nodes
resource "vsphere_virtual_machine" "demo_nodes" {
  count            = length(var.demo_node_ips)
  name             = "demo-k8s-node${count.index}"
  resource_pool_id = data.vsphere_host.host.resource_pool_id
  datastore_id     = data.vsphere_datastore.datastore.id
  # 第一个节点用于运行jmeter-master，所以cpu需要多点
  num_cpus         = "${count.index == 0 ? 8 : 4}"
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
        host_name = "demo-k8s-node${count.index}"
        domain    = "demo-k8s-node${count.index}"
      }
      network_interface {
        ipv4_address = var.demo_node_ips[count.index]
        ipv4_netmask = 24
      }
      ipv4_gateway = var.ipv4_gateway
    }
  }

  depends_on = [null_resource.init_master]
}

resource "null_resource" "init_nodes" {
  count = length(var.demo_node_ips)

  triggers = {
    always_run = timestamp()
  }

  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = var.ssh_user
      password = var.ssh_password
      host     = vsphere_virtual_machine.demo_ansible.default_ip_address
    }

    inline = [
      "while ! nc -w 5 -z ${var.demo_node_ips[count.index]} 22; do echo 'retry until ${var.demo_node_ips[count.index]} ssh ready ...'; sleep 5; done",
      "dcli k8s install --install=y --target_host=${var.demo_node_ips[count.index]} --target_host_password='${var.ssh_password}' --mode=worker --hostip=${var.demo_node_ips[count.index]} --hostname=${vsphere_virtual_machine.demo_nodes[count.index].clone[0].customize[0].linux_options[0].host_name}"
    ]
  }

  depends_on = [vsphere_virtual_machine.demo_nodes]
}

resource "null_resource" "get_k8s_cluster_join_info" {
  triggers = {
    always_run = timestamp()
  }

  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = var.ssh_user
      password = var.ssh_password
      host     = vsphere_virtual_machine.demo_ansible.default_ip_address
    }

    inline = [
      "sshpass -p '${var.ssh_password}' ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${var.demo_master_ip} kubeadm token create --print-join-command > /tmp/.temp.txt"
    ]
  }

  depends_on = [null_resource.init_nodes]
}
resource "null_resource" "join_k8s_node_to_master" {
  count = length(var.demo_node_ips)
  triggers = {
    always_run = timestamp()
  }

  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = var.ssh_user
      password = var.ssh_password
      host     = vsphere_virtual_machine.demo_ansible.default_ip_address
    }

    inline = [
      "cat /tmp/.temp.txt | sshpass -p '${var.ssh_password}' ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${var.demo_node_ips[count.index]} || true"
    ]
  }

  depends_on = [null_resource.get_k8s_cluster_join_info]
}

# 创建 demo-k8s-nfs
resource "vsphere_virtual_machine" "demo_nfs" {
  name             = "demo-k8s-nfs"
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
        host_name = "demo-k8s-nfs"
        domain    = "demo-k8s-nfs"
      }
      network_interface {
        ipv4_address = var.demo_nfs_ip
        ipv4_netmask = 24
      }
      ipv4_gateway = var.ipv4_gateway
    }
  }

  depends_on = [null_resource.join_k8s_node_to_master]
}

resource "null_resource" "init_nfs" {
  triggers = {
    always_run = timestamp()
  }

  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = var.ssh_user
      password = var.ssh_password
      host     = var.demo_ansible_ip
    }

    inline = [
      "sshpass -p '${var.ssh_password}' ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${var.demo_nfs_ip} \"yum install nfs-utils -y; systemctl start nfs-server; systemctl enable nfs-server; mkdir -p /data; echo '/data *(rw,sync,no_root_squash,no_subtree_check)' > /etc/exports; exportfs -a;\""
    ]
  }

  depends_on = [vsphere_virtual_machine.demo_nfs]
}
