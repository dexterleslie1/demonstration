provider "aws" {
  # 日本东京
  region = "ap-northeast-1"
}

resource "aws_vpc" "demo_vpc" {
  cidr_block = "192.168.0.0/16"

  tags = {
    Name = "demo_vpc"
  }
}