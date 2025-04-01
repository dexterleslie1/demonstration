// 演示external provider的用法
// https://registry.terraform.io/providers/hashicorp/external/latest/docs/data-sources/external
// https://jkrsp.com/extending-terraform-with-external-data-sources/

data "external" "js" {
    program = ["node", "${path.module}/data-source.js"]
}

output "external_source" {
  value = data.external.js.result
}

