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

# 使用默认参数创建密钥
# https://registry.terraform.io/providers/hashicorp/aws/3.26.0/docs/resources/kms_key
resource "aws_kms_key" "demo_aws_kms_key1" {
  description = "demo-aws-kms-key1"
}
