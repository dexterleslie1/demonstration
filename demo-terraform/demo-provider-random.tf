# 演示random provider用法
# https://registry.terraform.io/providers/hashicorp/random/latest/docs

# 演示random_shuffle资源用法
resource "random_shuffle" "my_shuffle" {
  input        = ["1", "2", "3", "4", "5"]
  result_count = 2
}

output "output_demo_random_shuffle" {
  value = random_shuffle.my_shuffle.result
}

