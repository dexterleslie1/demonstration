# NOTE: 8个slave性能能够达到预期，32w/s QPS，需要把master cpu调高，否则master是瓶颈
variable "ssh_user" {
  type    = string
  default = "centos"
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
  default = ["192.168.1.187"/*, "192.168.1.188", "192.168.1.189", "192.168.1.190", "192.168.1.191", "192.168.1.192", "192.168.1.193", "192.168.1.194" */]
}


provider "google" {
  project = "xyfront-357718"

  # region和zone列表
  region = "asia-east2"
  zone   = "asia-east2-a"
}

resource "google_compute_network" "demo_jmeter_vpc" {
  name = "demo-jmeter-vpc"
}

resource "google_compute_firewall" "demo_jmeter_default_allow_internal" {
  name        = "demo-jmeter-default-allow-internal"
  description = "Allow internal traffic on the default network"
  priority    = 65534
  network     = google_compute_network.demo_jmeter_vpc.self_link

  allow {
    protocol = "tcp"
    ports    = ["0-65535"]
  }

  allow {
    protocol = "udp"
    ports    = ["0-65535"]
  }

  allow {
    protocol = "icmp"
  }

  source_ranges = ["192.168.0.0/16"]
}
resource "google_compute_firewall" "demo_jmeter_firewall_icmp" {
  name    = "demo-jmeter-firewall-icmp"
  network = google_compute_network.demo_jmeter_vpc.self_link

  allow {
    protocol = "icmp"
  }

  source_ranges = ["0.0.0.0/0"]
}
resource "google_compute_firewall" "demo_jmeter_firewall_22" {
  name    = "demo-jmeter-firewall-22"
  network = google_compute_network.demo_jmeter_vpc.self_link

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }

  source_ranges = ["0.0.0.0/0"]
}
resource "google_compute_firewall" "demo_jmeter_firewall_80" {
  name    = "demo-jmeter-firewall-80"
  network = google_compute_network.demo_jmeter_vpc.self_link

  allow {
    protocol = "tcp"
    ports    = ["80"]
  }

  source_ranges = ["0.0.0.0/0"]
}

resource "google_compute_subnetwork" "demo_jmeter_subnet" {
  name          = "demo-jmeter-subnet"
  ip_cidr_range = "192.168.0.0/16"
  network       = google_compute_network.demo_jmeter_vpc.id
}

data "google_compute_image" "centos8" {
  family      = "centos-stream-8"
  project     = "centos-cloud"
  most_recent = true
}

resource "google_compute_instance" "demo_jmeter_ansible_vm" {
  name                      = "demo-jmeter-ansible"
  allow_stopping_for_update = true
  desired_status            = "RUNNING"
  machine_type              = "custom-2-2048"

  boot_disk {
    auto_delete = true
    initialize_params {
      size  = 20
      type  = "pd-ssd"
      image = data.google_compute_image.centos8.self_link
    }
  }

  network_interface {
    subnetwork = google_compute_subnetwork.demo_jmeter_subnet.self_link
    network_ip = var.vm_ansible_ip
    // 自动分配外网ip
    access_config {

    }
  }

  metadata = {
    ssh-keys = "${var.ssh_user}:${file("${path.module}/public.key")}"
  }

  provisioner "remote-exec" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = self.network_interface[0].access_config[0].nat_ip
    }

    inline = [
      "sudo mkdir -p /usr/local/my-workspace",
      "sudo mkdir -p /root/.ssh",
      "sudo chown -R ${var.ssh_user}:${var.ssh_user} /usr/local/my-workspace"
    ]
  }

  # 复制demo-jmeter-customize-plugin-1.0.0.jar
  provisioner "file" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = self.network_interface[0].access_config[0].nat_ip
    }

    source      = "../../demo-jmeter-customize-plugin/target/demo-jmeter-customize-plugin-1.0.0.jar"
    destination = "/usr/local/my-workspace/demo-jmeter-customize-plugin-1.0.0.jar"
  }
  # 复制demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar
  provisioner "file" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = self.network_interface[0].access_config[0].nat_ip
    }

    source      = "../../demo-jmeter-customize-plugin/target/demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar"
    destination = "/usr/local/my-workspace/demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar"
  }
  # 复制jmeter.jmx
  provisioner "file" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = self.network_interface[0].access_config[0].nat_ip
    }

    source      = "../../demo-jmeter-customize-plugin/jmeter.jmx"
    destination = "/usr/local/my-workspace/jmeter.jmx"
  }

  # 复制playbooks到anisble vm
  provisioner "file" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = self.network_interface[0].access_config[0].nat_ip
    }

    source      = "../setup.sh"
    destination = "/usr/local/my-workspace/setup.sh"
  }

  provisioner "file" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = self.network_interface[0].access_config[0].nat_ip
    }

    source      = "./private.key"
    destination = "/usr/local/my-workspace/private.key"
  }

  provisioner "remote-exec" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = self.network_interface[0].access_config[0].nat_ip
    }

    inline = [
      "sudo sh /usr/local/my-workspace/setup.sh",

      # 配置ansible主机免密码登录到其他jmeter主机
      "sudo mv /usr/local/my-workspace/private.key /root/.ssh/id_rsa",
      "sudo chmod 600 /root/.ssh/id_rsa",
      "sudo yum install nc -y",
    ]
  }
}

resource "google_compute_instance" "demo_jmeter_master_vm" {
  name                      = "demo-jmeter-master"
  allow_stopping_for_update = true
  desired_status            = "RUNNING"
  machine_type              = "custom-16-14848"

  boot_disk {
    auto_delete = true
    initialize_params {
      size  = 20
      type  = "pd-ssd"
      image = data.google_compute_image.centos8.self_link
    }
  }

  network_interface {
    subnetwork = google_compute_subnetwork.demo_jmeter_subnet.self_link
    network_ip = var.vm_jmeter_master_ip
    // 自动分配外网ip
    access_config {

    }
  }

  # 分配ssh key
  metadata = {
    ssh-keys = "${var.ssh_user}:${file("${path.module}/public.key")}"
  }

  depends_on = [google_compute_instance.demo_jmeter_ansible_vm]
}

resource "null_resource" "init_jmeter_master" {
  triggers = {
    # 因为这个属性每次运行都变化，所以每次执行apply都会运行这个null_resource
    always_run = "${timestamp()}"
  }

  # 等待实例ssh ready
  provisioner "remote-exec" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = google_compute_instance.demo_jmeter_ansible_vm.network_interface[0].access_config[0].nat_ip
    }

    inline = [
      "while ! nc -w 5 -z ${google_compute_instance.demo_jmeter_master_vm.network_interface[0].network_ip} 22; do echo 'retry until ${google_compute_instance.demo_jmeter_master_vm.network_interface[0].network_ip} ssh ready ...'; sleep 5; done",
      "echo '${google_compute_instance.demo_jmeter_master_vm.network_interface[0].network_ip} ssh is ready!'"
    ]
  }

  # 配置jmeter master
  provisioner "remote-exec" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = google_compute_instance.demo_jmeter_ansible_vm.network_interface[0].access_config[0].nat_ip
    }

    inline = [
      "sudo dcli jmeter install --install=y --target_host=${google_compute_instance.demo_jmeter_master_vm.network_interface[0].network_ip} --target_host_user=${var.ssh_user} --mode=master --remote_hosts=${join(",", var.vm_jmeter_slave_ips)} -xmx=6",
      # 复制demo-jmeter-customize-plugin-1.0.0.jar
      # 复制demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar
      # https://serverfault.com/questions/330503/scp-without-known-hosts-check
      "sudo scp -o StrictHostKeyChecking=no /usr/local/my-workspace/demo-jmeter-customize-plugin-1.0.0.jar ${var.ssh_user}@${google_compute_instance.demo_jmeter_master_vm.network_interface[0].network_ip}:demo-jmeter-customize-plugin-1.0.0.jar",
      "sudo scp -o StrictHostKeyChecking=no /usr/local/my-workspace/demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar ${var.ssh_user}@${google_compute_instance.demo_jmeter_master_vm.network_interface[0].network_ip}:demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar",
      "sudo ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${google_compute_instance.demo_jmeter_master_vm.network_interface[0].network_ip} sudo mv demo-jmeter-customize-plugin-1.0.0.jar /usr/local/software/jmeter/lib/ext/demo-jmeter-customize-plugin-1.0.0.jar",
      "sudo ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${google_compute_instance.demo_jmeter_master_vm.network_interface[0].network_ip} sudo mv demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar /usr/local/software/jmeter/lib/ext/demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar",
      "sudo ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${google_compute_instance.demo_jmeter_master_vm.network_interface[0].network_ip} sudo chown root:root /usr/local/software/jmeter/lib/ext/demo-jmeter*.jar",
      # 复制jmeter.jmx
      "sudo scp -o StrictHostKeyChecking=no /usr/local/my-workspace/jmeter.jmx ${var.ssh_user}@${google_compute_instance.demo_jmeter_master_vm.network_interface[0].network_ip}:jmeter.jmx",
    ]
  }

  // 等待资源准备好才执行此资源
  depends_on = [google_compute_instance.demo_jmeter_master_vm]
}

resource "google_compute_instance" "demo_jmeter_slave_vm" {
  count                     = length(var.vm_jmeter_slave_ips)
  name                      = "demo-jmeter-slave-${count.index}"
  allow_stopping_for_update = true
  desired_status            = "RUNNING"
  machine_type              = "custom-4-6144"

  boot_disk {
    auto_delete = true
    initialize_params {
      size  = 20
      type  = "pd-ssd"
      image = data.google_compute_image.centos8.self_link
    }
  }

  network_interface {
    subnetwork = google_compute_subnetwork.demo_jmeter_subnet.self_link
    network_ip = var.vm_jmeter_slave_ips[count.index]
    // 自动分配外网ip
    access_config {

    }
  }

  # 分配ssh key
  metadata = {
    ssh-keys = "${var.ssh_user}:${file("${path.module}/public.key")}"
  }

  depends_on = [null_resource.init_jmeter_master]
}

resource "null_resource" "init_jmeter_slave" {
  count = length(var.vm_jmeter_slave_ips)

  triggers = {
    # 因为这个属性每次运行都变化，所以每次执行apply都会运行这个null_resource
    always_run = "${timestamp()}"
  }

  # 等待实例ssh ready
  provisioner "remote-exec" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = google_compute_instance.demo_jmeter_ansible_vm.network_interface[0].access_config[0].nat_ip
    }

    inline = [
      "while ! nc -w 5 -z ${google_compute_instance.demo_jmeter_slave_vm[count.index].network_interface[0].network_ip} 22; do echo 'retry until ${google_compute_instance.demo_jmeter_slave_vm[count.index].network_interface[0].network_ip} ssh ready ...'; sleep 5; done",
      "echo '${google_compute_instance.demo_jmeter_slave_vm[count.index].network_interface[0].network_ip} ssh is ready!'"
    ]
  }

  # 配置jmeter slave
  provisioner "remote-exec" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = google_compute_instance.demo_jmeter_ansible_vm.network_interface[0].access_config[0].nat_ip
    }

    inline = [
      "sudo dcli jmeter install --install=y --target_host=${var.vm_jmeter_slave_ips[count.index]} --target_host_user=${var.ssh_user} --mode=slave --slave_listen_ip=${var.vm_jmeter_slave_ips[count.index]} -xmx=4",
      # 复制demo-jmeter-customize-plugin-1.0.0.jar
      # 复制demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar
      "sudo scp -o StrictHostKeyChecking=no /usr/local/my-workspace/demo-jmeter-customize-plugin-1.0.0.jar ${var.ssh_user}@${var.vm_jmeter_slave_ips[count.index]}:demo-jmeter-customize-plugin-1.0.0.jar",
      "sudo scp -o StrictHostKeyChecking=no /usr/local/my-workspace/demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar ${var.ssh_user}@${var.vm_jmeter_slave_ips[count.index]}:demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar",
      "sudo ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${var.vm_jmeter_slave_ips[count.index]} sudo mv demo-jmeter-customize-plugin-1.0.0.jar /usr/local/software/jmeter/lib/ext/demo-jmeter-customize-plugin-1.0.0.jar",
      "sudo ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${var.vm_jmeter_slave_ips[count.index]} sudo mv demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar /usr/local/software/jmeter/lib/ext/demo-jmeter-customize-plugin-1.0.0-jar-with-dependencies.jar",
      "sudo ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${var.vm_jmeter_slave_ips[count.index]} sudo chown root:root /usr/local/software/jmeter/lib/ext/demo-jmeter*.jar",
      # 重新启动jmeter slave否则因为没有加载插件报错
      "sudo ssh -o StrictHostKeyChecking=no ${var.ssh_user}@${var.vm_jmeter_slave_ips[count.index]} sudo systemctl restart jmeter-server",
    ]
  }

  // 等待资源准备好才执行此资源
  depends_on = [google_compute_instance.demo_jmeter_slave_vm]
}
