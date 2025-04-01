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

# 创建s3 bucket
# https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket
resource "aws_s3_bucket" "demo_aws_s3_bucket1" {
  bucket = "demo-aws-s3-bucket1"
  # 当bucket销毁时所有bucket内所有object都被一同销毁
  force_destroy = true
}

# 启用版本记录特性
# https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_versioning
resource "aws_s3_bucket_versioning" "demo_aws_s3_bucket_versioning1" {
  bucket = aws_s3_bucket.demo_aws_s3_bucket1.id

  versioning_configuration {
    status = "Enabled"
  }
}

# s3加密相关配置
# https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_server_side_encryption_configuration
resource "aws_s3_bucket_server_side_encryption_configuration" "demo_aws_s3_bucket_server_side_encryption_configuration1" {
  bucket = aws_s3_bucket.demo_aws_s3_bucket1.id

  rule {
    # 当使用 KMS 加密来加密此存储桶中的新对象时，存储桶密钥会减少对 AWS KMS 的调用，从而降低加密成本。
    bucket_key_enabled = true
  }
}

# 屏蔽公共访问权限（存储桶设置）
# https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_public_access_block
resource "aws_s3_bucket_public_access_block" "demo_aws_s3_bucket_public_access_block1" {
  bucket = aws_s3_bucket.demo_aws_s3_bucket1.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  # 启用阻止所有公开访问
  restrict_public_buckets = true
}

