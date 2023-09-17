# 演示meta-arguments foreach用法
# https://developer.hashicorp.com/terraform/language/meta-arguments/for_each

resource "null_resource" "null_resource_foreach_map" {
  for_each = {
    "k1" = "v1"
    "k2" = "v2"
  }

  provisioner "local-exec" {
    command = "echo '${each.key} - ${each.value}'"
  }
}

resource "null_resource" "null_resource_foreach_list" {
  for_each = toset(["k1", "k2"])
  provisioner "local-exec" {
    command = "echo '${each.key}'"
  }
}
