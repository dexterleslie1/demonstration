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

# 创建jmeter master vm
resource "vsphere_virtual_machine" "demo_vm1" {
  name               = "demo-vm1"
  resource_pool_id   = data.vsphere_host.host.resource_pool_id
  datastore_id       = data.vsphere_datastore.datastore.id
  num_cpus           = 4
  memory             = 4096
  cpu_limit          = (4 * 2200)
  cpu_reservation    = (4 * 2200)
  cpu_share_level    = "custom"
  cpu_share_count    = (4 * 2200)
  memory_limit       = 4096
  memory_reservation = 4096
  memory_share_level = "custom"
  memory_share_count = (4096 * 20)
  guest_id           = data.vsphere_virtual_machine.template.guest_id
  scsi_type          = data.vsphere_virtual_machine.template.scsi_type
  # folder             = "/${data.vsphere_datacenter.datacenter.name}/${var.vm_folder}"
  folder             = "${var.vm_folder}"
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
        host_name = "demo-vm1"
        domain    = "demo-vm1"
      }
      network_interface {
        ipv4_address = var.demo_vm1_ip
        ipv4_netmask = 24
      }
      ipv4_gateway = var.ipv4_gateway
    }
  }
}

# 在所有虚拟机创建成功后执行脚本
resource "null_resource" "init_demo_vm1" {
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
      host     = var.demo_vm1_ip
    }

    inline = [
      "while ! nc -w 5 -z ${var.demo_vm1_ip} 22; do echo 'retry until ${var.demo_vm1_ip} ssh ready ...'; sleep 5; done",
      "echo '${var.demo_vm1_ip} ssh is ready!'"
    ]
  }

  # 配置jmeter master
  provisioner "remote-exec" {
    connection {
      type     = "ssh"
      user     = var.ssh_user
      password = var.vsphere_template_password
      host     = var.demo_vm1_ip
    }

    inline = [
      "date",
    ]
  }

  // 等待资源准备好才执行此资源
  depends_on = [resource.vsphere_virtual_machine.demo_vm1]
}
