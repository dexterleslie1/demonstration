# 演示terraform模块化

variable "my_aws_region" {
  type    = string
  default = "ap-northeast-1"
}

provider "aws" {
  region = var.my_aws_region
}

locals {
  policy_mapping = {
    "app1" = {
      policies = [jsonencode(
        {
          "Version" : "2012-10-17",
          "Statement" : [
            {
              "Action" : [
                "ec2:Describe"
              ],
              "Effect" : "Allow",
              "Resource" : "*"
            }
          ]
        }), jsonencode(
        {
          "Version" : "2012-10-17",
          "Statement" : [
            {
              "Action" : [
                "ec2:Describe"
              ],
              "Effect" : "Allow",
              "Resource" : "*"
            }
          ]
      })]
    },
    "app2" = {
      policies = [jsonencode(
        {
          "Version" : "2012-10-17",
          "Statement" : [
            {
              "Action" : [
                "ec2:Describe"
              ],
              "Effect" : "Allow",
              "Resource" : "*"
            }
          ]
        }), jsonencode(
        {
          "Version" : "2012-10-17",
          "Statement" : [
            {
              "Action" : [
                "ec2:Describe"
              ],
              "Effect" : "Allow",
              "Resource" : "*"
            }
          ]
      })]
    }
  }
}

module "iam" {
  source            = "./modules/iam"
  for_each          = local.policy_mapping
  iam_user_name     = each.key
  iam_user_policies = each.value.policies
}

# resource "local_file" "credentails" {
#   for_each = module.iam
#   filename          = each.value.iam_user_name
#   file_permission   = "0644"
#   sensitive_content = each.value.credentails
# }

# output "test" {
#     value = module.iam["app1"].iam_user_name
# }

resource "local_file" "credentails" {
    filename = "credentails"
    file_permission = "0644"
    content = join("\n", [for m in module.iam: m.credentails])
}
