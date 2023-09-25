// https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/regions

variable "my_aws_region" {
  type    = string
  default = "ap-northeast-1"
}

provider "aws" {
  region = var.my_aws_region
}

// list Enabled AWS Regions
data "aws_regions" "demo_enable_regions" {}

// All the regions regardless of the availability
data "aws_regions" "demo_all_regions" {
  all_regions = true
}

output "my_output_enable" {
    value = data.aws_regions.demo_enable_regions
}
output "my_output_all" {
    value = data.aws_regions.demo_all_regions
}
