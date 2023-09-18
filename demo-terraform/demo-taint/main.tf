# 演示terraform模块化

variable "my_aws_region" {
  type    = string
  default = "ap-northeast-1"
}
variable "ssh_user" {
  type    = string
  default = "centos"
}

provider "aws" {
  region = var.my_aws_region
}

resource "aws_iam_user" "app1" {
    name = "app1-svc-account"
    force_destroy = true
}
resource "aws_iam_user_policy" "app1" {
    user = aws_iam_user.app1.name
    policy = <<-EOF
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Action": [
                    "ec2:Describe"
                ],
                "Effect": "Allow",
                "Resource": "*"
            }
        ]
    }
EOF
}
resource "aws_iam_access_key" "app1" {
    user = aws_iam_user.app1.name
}

resource "local_file" "credentails" {
    filename = "credentails"
    file_permission = "0644"
    sensitive_content = <<-EOF
    [${aws_iam_user.app1.name}]
    aws_access_key_id = ${aws_iam_user.app1.id}
    aws_secret_access_key = ${aws_iam_access_key.app1.secret}
EOF
}
