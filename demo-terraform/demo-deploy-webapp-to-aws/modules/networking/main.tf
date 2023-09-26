# 创建vpc
resource "aws_vpc" "demo_vpc" {
  cidr_block = "192.168.0.0/16"

  # 是否启用DNS 解析
  enable_dns_support = true
  # 是否启用DNS 主机名
  enable_dns_hostnames = true

  tags = {
    Name = "${var.my_project}-vpc"
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
    Name = "${var.my_project}-default-route-table"
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
    Name = "${var.my_project}-subnet"
  }
}
resource "aws_subnet" "demo_subnet1" {
  vpc_id     = aws_vpc.demo_vpc.id
  cidr_block = "192.168.2.0/24"
  # 自动分配公有 IPv4 地址
  map_public_ip_on_launch = true

  # 指定子网availability_zone相当于指定vm的availability_zone
  availability_zone = "${var.my_aws_region}c"

  tags = {
    Name = "${var.my_project}-subnet1"
  }
}

# 创建internet gateway
resource "aws_internet_gateway" "demo_internet_gateway" {
  vpc_id = aws_vpc.demo_vpc.id

  tags = {
    Name = "${var.my_project}-internet-gateway"
  }
}

# 创建自定义security group
resource "aws_security_group" "demo_security_group" {
  name   = "${var.my_project}-security-group"
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
