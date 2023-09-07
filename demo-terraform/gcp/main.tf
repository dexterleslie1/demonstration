variable "ssh_user" {
  type    = string
  default = "centos"
}

provider "google" {
  project = "xyfront-357718"

  # region和zone列表
  # https://cloud.google.com/compute/docs/regions-zones
  region = "asia-east2"
  zone   = "asia-east2-a"
}

# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/compute_network
resource "google_compute_network" "demo_vpc1" {
  name = "demo-vpc1"
}

# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/compute_firewall
# 允许内网所有浏览
resource "google_compute_firewall" "demo_default_allow_internal" {
  name        = "demo-default-allow-internal"
  description = "Allow internal traffic on the default network"
  priority    = 65534
  network     = google_compute_network.demo_vpc1.self_link

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
resource "google_compute_firewall" "demo_firewall_allow_icmp" {
  name    = "demo-firewall-allow-icmp"
  network = google_compute_network.demo_vpc1.self_link

  allow {
    protocol = "icmp"
  }

  # source_tags = ["web"]
  # https://googlecloudarchitect.us/firewall-rules-in-gcp-via-terraform/
  source_ranges = ["0.0.0.0/0"]
}
resource "google_compute_firewall" "demo_firewall_allow_22" {
  name    = "demo-firewall-allow-22"
  network = google_compute_network.demo_vpc1.self_link

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }

  # https://googlecloudarchitect.us/firewall-rules-in-gcp-via-terraform/
  source_ranges = ["0.0.0.0/0"]
}
resource "google_compute_firewall" "demo_firewall_allow_80" {
  name    = "demo-firewall-allow-80"
  network = google_compute_network.demo_vpc1.self_link

  allow {
    protocol = "tcp"
    ports    = ["80"]
  }

  # https://googlecloudarchitect.us/firewall-rules-in-gcp-via-terraform/
  source_ranges = ["0.0.0.0/0"]
}

# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/compute_subnetwork
resource "google_compute_subnetwork" "demo_subnet1" {
  name          = "demo-subnet1"
  ip_cidr_range = "192.168.0.0/16"
  # region        = "us-central1"
  network = google_compute_network.demo_vpc1.id
}

# 镜像列表
# https://cloud.google.com/compute/docs/images/os-details
# https://registry.terraform.io/providers/hashicorp/google/latest/docs/data-sources/compute_image
data "google_compute_image" "centos8" {
  family      = "centos-stream-8"
  project     = "centos-cloud"
  most_recent = true
}

# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/compute_instance#nested_access_config
resource "google_compute_instance" "demo_vm1" {
  name                      = "demo-vm1"
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

  # https://stackoverflow.com/questions/63401480/how-to-create-gcp-instance-with-public-ip-with-terraform
  network_interface {
    # network = "default"
    subnetwork = google_compute_subnetwork.demo_subnet1.self_link
    network_ip = "192.168.1.185"
    // 自动分配外网ip
    access_config {

    }
  }

  # 分配ssh key
  # https://stackoverflow.com/questions/38645002/how-to-add-an-ssh-key-to-an-gcp-instance-using-terraform
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
      # 配置ansible主机免密码登录到其他jmeter主机
      "sudo mv /usr/local/my-workspace/private.key /root/.ssh/id_rsa",
      "sudo chmod 600 /root/.ssh/id_rsa",
      "sudo yum install nc -y"
    ]
  }
}

resource "google_compute_instance" "demo_vm2" {
  name                      = "demo-vm2"
  allow_stopping_for_update = true
  desired_status            = "RUNNING"
  machine_type              = "e2-medium"

  boot_disk {
    auto_delete = true
    initialize_params {
      size  = 20
      type  = "pd-ssd"
      image = data.google_compute_image.centos8.self_link
    }
  }

  network_interface {
    subnetwork = google_compute_subnetwork.demo_subnet1.self_link
    network_ip = "192.168.1.186"
    // 自动分配外网ip
    access_config {

    }
  }

  # 分配ssh key
  metadata = {
    ssh-keys = "${var.ssh_user}:${file("${path.module}/public.key")}"
  }

  depends_on = [google_compute_instance.demo_vm1]
}

resource "null_resource" "wait_until_demo_vm2_ssh_ready" {
  triggers = {
    always_run = "${timestamp()}"
  }

  # 等待实例ssh ready
  # https://www.reddit.com/r/Terraform/comments/wl5nml/solutions_to_wait_till_ec2_instance_is_ready/
  provisioner "remote-exec" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = google_compute_instance.demo_vm1.network_interface[0].access_config[0].nat_ip
    }

    inline = [
      "while ! nc -w 5 -z ${google_compute_instance.demo_vm2.network_interface[0].network_ip} 22; do echo 'retry until ${google_compute_instance.demo_vm2.network_interface[0].network_ip} ssh ready ...'; sleep 5; done",
      "echo '${google_compute_instance.demo_vm2.network_interface[0].network_ip} ssh is ready!'"
    ]
  }

  depends_on = [google_compute_instance.demo_vm2]
}
