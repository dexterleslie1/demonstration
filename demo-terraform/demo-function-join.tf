# 演示join函数用法
variable "my_var_list_1" {
  type    = list(string)
  default = ["1", "2", "3"]
}

output "my_output_function_join_1" {
  value = join(",", var.my_var_list_1)
}
