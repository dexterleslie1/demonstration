# 演示使用ssh key连接主机
variable "my_aws_region" {
  type    = string
  default = "ap-northeast-1"
}

provider "aws" {
  region = var.my_aws_region
}

data "aws_ami" "centos8" {
  owners      = ["125523088429"]
  most_recent = true
  name_regex  = "CentOS Stream 8 x86_64 20230830"
}

# 创建vpc
resource "aws_vpc" "demo_vpc" {
  cidr_block = "192.168.0.0/16"
  tags = {
    Name = "demo_vpc"
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
    Name = "demo_default_route_table"
  }
}

# 创建子网
resource "aws_subnet" "demo_subnet" {
  vpc_id     = aws_vpc.demo_vpc.id
  cidr_block = "192.168.1.0/24"
  # 自动分配公有 IPv4 地址
  map_public_ip_on_launch = true
  
  # 指定子网availability_zone相当于指定vm的availability_zone
  availability_zone = "${var.my_aws_region}a"

  tags = {
    Name = "demo_subnet"
  }
}

# 创建internet gateway
resource "aws_internet_gateway" "demo_internet_gateway" {
  vpc_id = aws_vpc.demo_vpc.id

  tags = {
    Name = "demo_internet_gateway"
  }
}

# 创建自定义security group
resource "aws_security_group" "demo_security_group" {
  name   = "demo_security_group"
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

# 创建key pair
# https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/key_pair
resource "aws_key_pair" "demo_key" {
  key_name   = "demo_key"
  public_key = file("${path.module}/public.key")
}

# https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/instance#vpc_security_group_ids
resource "aws_instance" "demo_vm1" {
  ami = data.aws_ami.centos8.id

  private_ip = "192.168.1.185"

  # 系统卷配置
  root_block_device {
    volume_size = 20
  }

  vpc_security_group_ids = [resource.aws_security_group.demo_security_group.id]
  # 需要指定vm的子网，否则会报告security_group和subnet不属于同一个网络错误
  subnet_id = aws_subnet.demo_subnet.id

  key_name = "demo_key"

  # cpu=4,memory=7.5G
  # instance_type = "c4.xlarge"
  instance_type = "t2.small"

  tags = {
    Name = "demo_vm1"
  }
}

resource "null_resource" "init_vm" {
  triggers = {
    always_run = "${timestamp()}"
  }

  provisioner "file" {
    connection {
        type     = "ssh"
        user     = "centos"
        private_key = file("${path.module}/private.key")
        host     = aws_instance.demo_vm1.public_ip
    }

    source      = "../README.md"
    destination = "/home/centos/README.md"
  }

}

# 绑定eip到vm
resource "aws_eip" "demo_eip" {
  instance = aws_instance.demo_vm1.id
  domain   = "vpc"

  depends_on = [ null_resource.init_vm ]
}
