# NOTE: 不知道为何没有发挥出8个slave的性能
variable "aws_region" {
  type    = string
  default = "ap-northeast-1"
}
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
  default = ["192.168.1.187", "192.168.1.188", "192.168.1.189", "192.168.1.190", "192.168.1.191", "192.168.1.192", "192.168.1.193", "192.168.1.194" /**/]
}

provider "aws" {
  region = var.aws_region
}

data "aws_ami" "centos8" {
  owners      = ["125523088429"]
  most_recent = true
  name_regex  = "CentOS Stream 8 x86_64 20230830"
}

# 创建vpc
resource "aws_vpc" "demo_vpc" {
  cidr_block = "192.168.0.0/16"

  # 是否启用DNS 解析
  enable_dns_support = true
  # 是否启用DNS 主机名
  enable_dns_hostnames = true

  tags = {
    Name = "demo-jmeter-vpc"
  }
}

# 编辑vpc的默认路由表
resource "aws_default_route_table" "demo_default_route_table" {
  default_route_table_id = aws_vpc.demo_vpc.default_route_table_id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.demo_internet_gateway.id
  }

  tags = {
    Name = "demo-jmeter-default-route-table"
  }
}

# 创建子网
resource "aws_subnet" "demo_subnet" {
  vpc_id     = aws_vpc.demo_vpc.id
  cidr_block = "192.168.1.0/24"
  # 自动分配公有 IPv4 地址
  map_public_ip_on_launch = true

  # 指定子网availability_zone相当于指定vm的availability_zone
  availability_zone = "${var.aws_region}a"

  tags = {
    Name = "demo-jmeter-subnet"
  }
}

# 创建internet gateway
resource "aws_internet_gateway" "demo_internet_gateway" {
  vpc_id = aws_vpc.demo_vpc.id

  tags = {
    Name = "demo-jmeter-internet-gateway"
  }
}

# 创建自定义security group
resource "aws_security_group" "demo_security_group" {
  name   = "demo-jmeter-security-group"
  vpc_id = aws_vpc.demo_vpc.id

  ingress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }
}

resource "aws_key_pair" "demo_key" {
  key_name   = "demo-jmeter-key"
  public_key = file("${path.module}/public.key")
}

resource "aws_instance" "demo_ansible_vm" {
  ami = data.aws_ami.centos8.id

  private_ip = var.vm_ansible_ip

  # 系统卷配置
  root_block_device {
    volume_size = 20
  }

  vpc_security_group_ids = [resource.aws_security_group.demo_security_group.id]
  # 需要指定vm的子网，否则会报告security_group和subnet不属于同一个网络错误
  subnet_id = aws_subnet.demo_subnet.id

  key_name = aws_key_pair.demo_key.key_name

  instance_type = "t2.small"

  tags = {
    Name = "demo-jmeter-ansible"
  }

  # 保证/usr/local/my-workspace目录存在
  provisioner "remote-exec" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = self.public_ip
    }

    inline = [
      "sudo mkdir -p /usr/local/my-workspace",
      "sudo chown -R ${var.ssh_user}:${var.ssh_user} /usr/local/my-workspace"
    ]
  }

  # 复制playbooks到anisble vm
  provisioner "file" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = self.public_ip
    }

    source      = "../setup.sh"
    destination = "/usr/local/my-workspace/setup.sh"
  }

  provisioner "file" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = self.public_ip
    }

    source      = "./private.key"
    destination = "/usr/local/my-workspace/private.key"
  }

  provisioner "remote-exec" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = self.public_ip
    }

    inline = [
      "sudo sh /usr/local/my-workspace/setup.sh",

      # 配置ansible主机免密码登录到其他jmeter主机
      "sudo mv /usr/local/my-workspace/private.key /root/.ssh/id_rsa",
      "sudo chmod 600 /root/.ssh/id_rsa",
      "sudo yum install nc -y"
    ]
  }

}

resource "aws_instance" "demo_jmeter_master_vm" {
  ami = data.aws_ami.centos8.id

  private_ip = var.vm_jmeter_master_ip

  # 系统卷配置
  root_block_device {
    volume_size = 20
  }

  vpc_security_group_ids = [resource.aws_security_group.demo_security_group.id]
  # 需要指定vm的子网，否则会报告security_group和subnet不属于同一个网络错误
  subnet_id = aws_subnet.demo_subnet.id

  key_name = aws_key_pair.demo_key.key_name

  # cpu=16,memory=30G
  instance_type = "c3.4xlarge"

  tags = {
    Name = "demo-jmeter-master"
  }

  depends_on = [aws_instance.demo_ansible_vm]
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
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = aws_instance.demo_ansible_vm.public_ip
    }

    inline = [
      "while ! nc -w 5 -z ${aws_instance.demo_jmeter_master_vm.private_ip} 22; do echo 'retry until ${aws_instance.demo_jmeter_master_vm.private_ip} ssh ready ...'; sleep 2; done",
      "echo '${aws_instance.demo_jmeter_master_vm.private_ip} ssh is ready!'"
    ]
  }

  # 配置jmeter master
  provisioner "remote-exec" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = aws_instance.demo_ansible_vm.public_ip
    }

    inline = [
      "sudo dcli jmeter install --install=y --target_host=${var.vm_jmeter_master_ip} --target_host_user=${var.ssh_user} --mode=master --remote_hosts=${join(",", var.vm_jmeter_slave_ips)} -xmx=6"
    ]
  }

  // 等待资源准备好才执行此资源
  depends_on = [aws_instance.demo_jmeter_master_vm]
}

resource "aws_instance" "demo_jmeter_slave_vm" {
  count = length(var.vm_jmeter_slave_ips)
  ami   = data.aws_ami.centos8.id

  private_ip = var.vm_jmeter_slave_ips[count.index]

  # 系统卷配置
  root_block_device {
    volume_size = 20
  }

  vpc_security_group_ids = [resource.aws_security_group.demo_security_group.id]
  # 需要指定vm的子网，否则会报告security_group和subnet不属于同一个网络错误
  subnet_id = aws_subnet.demo_subnet.id

  key_name = aws_key_pair.demo_key.key_name

  # cpu=4,memory=7.5G
  instance_type = "c4.xlarge"

  tags = {
    Name = "demo-jmeter-slave-${count.index}"
  }

  depends_on = [null_resource.init_jmeter_master]
}

# 在所有虚拟机创建成功后执行脚本
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
      host        = aws_instance.demo_ansible_vm.public_ip
    }

    inline = [
      "while ! nc -w 5 -z ${aws_instance.demo_jmeter_slave_vm[count.index].private_ip} 22; do echo 'retry until ${aws_instance.demo_jmeter_slave_vm[count.index].private_ip} ssh ready ...'; sleep 2; done",
      "echo '${aws_instance.demo_jmeter_slave_vm[count.index].private_ip} ssh is ready!'"
    ]
  }

  # 配置jmeter slave
  provisioner "remote-exec" {
    connection {
      type        = "ssh"
      user        = var.ssh_user
      private_key = file("${path.module}/private.key")
      host        = aws_instance.demo_ansible_vm.public_ip
    }

    inline = [
      "sudo dcli jmeter install --install=y --target_host=${var.vm_jmeter_slave_ips[count.index]} --target_host_user=${var.ssh_user} --mode=slave --slave_listen_ip=${var.vm_jmeter_slave_ips[count.index]} -xmx=6"
    ]
  }

  // 等待资源准备好才执行此资源
  depends_on = [aws_instance.demo_jmeter_slave_vm]
}

# # 绑定eip到vm
# resource "aws_eip" "demo_jmeter_master_eip" {
#   instance = aws_instance.demo_jmeter_master_vm.id
#   domain   = "vpc"

#   depends_on = [null_resource.init_jmeter_slave]
# }

# 绑定eip到vm
resource "aws_eip" "demo_ansible_eip" {
  instance = aws_instance.demo_ansible_vm.id
  domain   = "vpc"

  depends_on = [null_resource.init_jmeter_slave]
}
