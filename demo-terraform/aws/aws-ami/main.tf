provider "aws" {
  region = "ap-northeast-1"
}

data "aws_ami" "centos8" {
  # 指定镜像所有者id
  owners      = ["125523088429"]
  most_recent = true
  # 指定镜像名称
  name_regex  = "CentOS Stream 8 x86_64 20230830"
}

output "ouput_aws_ami_centos8" {
  value = data.aws_ami.centos8
}
