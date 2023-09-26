data "cloudinit_config" "config" {
  gzip          = true
  base64_encode = true
  part {
    content_type = "text/cloud-config"
    content      = templatefile("${path.module}/cloud_config.yaml", var.my_db_config)
  }
}

data "aws_ami" "ubuntu" {
  most_recent = true
  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-bionic-18.04-amd64-server-*"]
  }
  owners = ["099720109477"]
}

resource "aws_key_pair" "key_pair" {
  key_name   = "${var.my_project}-key-pair"
  public_key = file("${path.module}/public.key")
}

resource "aws_launch_template" "webserver" {
  name_prefix   = var.my_project
  image_id      = data.aws_ami.ubuntu.id
  instance_type = "t2.micro"
  user_data     = data.cloudinit_config.config.rendered
  key_name      = aws_key_pair.key_pair.key_name
  vpc_security_group_ids = var.my_security_group_ids
}

resource "aws_autoscaling_group" "webserver" {
  name                = "${var.my_project}-autoscaling-group"
  min_size            = 1
  max_size            = 3
  vpc_zone_identifier = var.my_subnet_ids
  target_group_arns   = [aws_lb_target_group.demo_lb_target_group.arn]
  launch_template {
    id      = aws_launch_template.webserver.id
    version = aws_launch_template.webserver.latest_version
  }
}

// 创建application load balancer
// https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb
resource "aws_lb" "demo_lb" {
  name               = "${var.my_project}-lb1"
  internal           = false
  load_balancer_type = "application"
  security_groups    = var.my_security_group_ids
  subnets            = var.my_subnet_ids

  enable_deletion_protection = false

  tags = {
    Name = "${var.my_project}-lb1"
  }
}

// 配置load balancer listener
// https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_listener
resource "aws_lb_listener" "demo_lb_listener1" {
  load_balancer_arn = aws_lb.demo_lb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.demo_lb_target_group.arn
  }
}

// 配置load balancer target group
// https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lb_target_group
resource "aws_lb_target_group" "demo_lb_target_group" {
  name        = "${var.my_project}-lb-target-group"
  port        = 8080
  protocol    = "HTTP"
  target_type = "instance"
  vpc_id      = var.my_vpc_id
}

