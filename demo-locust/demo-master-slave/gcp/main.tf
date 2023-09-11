# NOTE: 性能达到预期，8个slave能够产生65w/s qps
variable "ssh_user" {
  type    = string
  default = "centos"
}
variable "vm_ansible_ip" {
  type    = string
  default = "192.168.1.185"
}
variable "vm_locust_master_ip" {
  type    = string
  default = "192.168.1.186"
}
variable "vm_locust_slave_ips" {
  type    = list(string)
  default = ["192.168.1.187", "192.168.1.188", "192.168.1.189", "192.168.1.190", "192.168.1.191", "192.168.1.192", "192.168.1.193", "192.168.1.194" /**/]
}


provider "google" {
  project = "xyfront-357718"

  # region和zone列表
  region = "asia-east2"
  zone   = "asia-east2-a"
}

resource "google_compute_network" "demo_locust_vpc" {
  name = "demo-locust-vpc"
}

resource "google_compute_firewall" "demo_locust_default_allow_internal" {
  name        = "demo-locust-default-allow-internal"
  description = "Allow internal traffic on the default network"
  priority    = 65534
  network     = google_compute_network.demo_locust_vpc.self_link

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
resource "google_compute_firewall" "demo_locust_firewall_icmp" {
  name    = "demo-locust-firewall-icmp"
  network = google_compute_network.demo_locust_vpc.self_link

  allow {
    protocol = "icmp"
  }

  source_ranges = ["0.0.0.0/0"]
}
resource "google_compute_firewall" "demo_locust_firewall_22" {
  name    = "demo-locust-firewall-22"
  network = google_compute_network.demo_locust_vpc.self_link

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }

  source_ranges = ["0.0.0.0/0"]
}
resource "google_compute_firewall" "demo_locust_firewall_80" {
  name    = "demo-locust-firewall-80"
  network = google_compute_network.demo_locust_vpc.self_link

  allow {
    protocol = "tcp"
    ports    = ["80"]
  }

  source_ranges = ["0.0.0.0/0"]
}
resource "google_compute_firewall" "demo_locust_firewall_8089" {
  name    = "demo-locust-firewall-8089"
  network = google_compute_network.demo_locust_vpc.self_link

  allow {
    protocol = "tcp"
    ports    = ["8089"]
  }

  source_ranges = ["0.0.0.0/0"]
}

resource "google_compute_subnetwork" "demo_locust_subnet" {
  name          = "demo-locust-subnet"
  ip_cidr_range = "192.168.0.0/16"
  network       = google_compute_network.demo_locust_vpc.id
}

data "google_compute_image" "centos8" {
  family      = "centos-stream-8"
  project     = "centos-cloud"
  most_recent = true
}

resource "google_compute_instance" "demo_locust_ansible_vm" {
  name                      = "demo-locust-ansible"
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
    subnetwork = google_compute_subnetwork.demo_locust_subnet.self_link
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

      # 配置ansible主机免密码登录到其他locust主机
      "sudo mv /usr/local/my-workspace/private.key /root/.ssh/id_rsa",
      "sudo chmod 600 /root/.ssh/id_rsa",
      "sudo yum install nc -y"
    ]
  }
}

resource "google_compute_instance" "demo_locust_master_vm" {
  name                      = "demo-locust-master"
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
    subnetwork = google_compute_subnetwork.demo_locust_subnet.self_link
    network_ip = var.vm_locust_master_ip
    // 自动分配外网ip
    access_config {

    }
  }

  # 分配ssh key
  metadata = {
    ssh-keys = "${var.ssh_user}:${file("${path.module}/public.key")}"
  }

  depends_on = [google_compute_instance.demo_locust_ansible_vm]
}

resource "null_resource" "init_locust_master" {
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
      host        = google_compute_instance.demo_locust_ansible_vm.network_interface[0].access_config[0].nat_ip
    }

    inline = [
      "while ! nc -w 5 -z ${google_compute_instance.demo_locust_master_vm.network_interface[0].network_ip} 22; do echo 'retry until ${google_compute_instance.demo_locust_master_vm.network_interface[0].network_ip} ssh ready ...'; sleep 5; done",
      "echo '${google_compute_instance.demo_locust_master_vm.network_interface[0].network_ip} ssh is ready!'"
    ]
  }

  # 配置locust master
  provisioner "remote-exec" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = google_compute_instance.demo_locust_ansible_vm.network_interface[0].access_config[0].nat_ip
    }

    inline = [
      "sudo dcli locust install --install=y --target_host=${google_compute_instance.demo_locust_master_vm.network_interface[0].network_ip} --target_host_user=${var.ssh_user} --mode=master"
    ]
  }

  // 等待资源准备好才执行此资源
  depends_on = [google_compute_instance.demo_locust_master_vm]
}

resource "google_compute_instance" "demo_locust_slave_vm" {
  count                     = length(var.vm_locust_slave_ips)
  name                      = "demo-locust-slave-${count.index}"
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
    subnetwork = google_compute_subnetwork.demo_locust_subnet.self_link
    network_ip = var.vm_locust_slave_ips[count.index]
    // 自动分配外网ip
    access_config {

    }
  }

  # 分配ssh key
  metadata = {
    ssh-keys = "${var.ssh_user}:${file("${path.module}/public.key")}"
  }

  depends_on = [null_resource.init_locust_master]
}

resource "null_resource" "init_locust_slave" {
  count = length(var.vm_locust_slave_ips)

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
      host        = google_compute_instance.demo_locust_ansible_vm.network_interface[0].access_config[0].nat_ip
    }

    inline = [
      "while ! nc -w 5 -z ${google_compute_instance.demo_locust_slave_vm[count.index].network_interface[0].network_ip} 22; do echo 'retry until ${google_compute_instance.demo_locust_slave_vm[count.index].network_interface[0].network_ip} ssh ready ...'; sleep 5; done",
      "echo '${google_compute_instance.demo_locust_slave_vm[count.index].network_interface[0].network_ip} ssh is ready!'"
    ]
  }

  # 配置locust slave
  provisioner "remote-exec" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = google_compute_instance.demo_locust_ansible_vm.network_interface[0].access_config[0].nat_ip
    }

    inline = [
      "sudo dcli locust install --install=y --target_host=${var.vm_locust_slave_ips[count.index]} --target_host_user=${var.ssh_user} --mode=slave --master_host=${var.vm_locust_master_ip} --slave_cpu_count=4"
    ]
  }

  // 等待资源准备好才执行此资源
  depends_on = [google_compute_instance.demo_locust_slave_vm]
}
