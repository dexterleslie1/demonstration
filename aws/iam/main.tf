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

# 创建用户组
# https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_group
resource "aws_iam_group" "demo_aws_iam_group" {
  name = "demo-aws-iam-group1"
}

# 创建用户
# https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_user
resource "aws_iam_user" "demo_aws_iam_user1" {
  name = "demo-aws-iam-user1"
}

# 创建策略JSON
# https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document
data "aws_iam_policy_document" "demo_aws_iam_policy_document" {
  statement {
    sid = "1"

    actions = [
      "s3:ListAllMyBuckets",
      "s3:GetBucketLocation",
    ]

    resources = [
      "arn:aws:s3:::*",
    ]
  }
}
data "aws_iam_policy_document" "demo_aws_iam_policy_document_assume" {
    statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["firehose.amazonaws.com"]
    }
  }
}
# 创建角色
# https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_role
resource "aws_iam_role" "demo_aws_iam_role" {
  name               = "demo-aws-iam-role"
  assume_role_policy = data.aws_iam_policy_document.demo_aws_iam_policy_document_assume.json
}

# 创建策略
# https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy
resource "aws_iam_policy" "demo_aws_iam_policy" {
  name   = "demo-aws-iam-policy"
  policy = data.aws_iam_policy_document.demo_aws_iam_policy_document.json
}

