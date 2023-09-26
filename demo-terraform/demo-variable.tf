// 演示个类型变量的用法

// list类型变量
variable "jmeter_master_ip" {
  type    = string
  default = "192.168.1.186"
}

variable "jmeter_salve_ips" {
  type = list(string)
  default = [
    "192.168.1.187",
    "192.168.1.188"
  ]
}

output "output_var_jmeter_master_ip" {
  value = var.jmeter_master_ip
}
output "output_var_jmeter_slave_ips" {
  value = var.jmeter_salve_ips
}

// 演示local变量
// https://developer.hashicorp.com/terraform/language/values/locals
// https://registry.terraform.io/providers/hashicorp/null/latest/docs/resources/resource
// https://stackoverflow.com/questions/52040798/terraform-outputs-for-resources-with-count
resource "null_resource" "x1" {
  count = 3
}
locals {
  // null_resource.x1.*.id表示把每个null_resource的id返回到all_null_resource list类型的本地变量中
  all_null_resource = [null_resource.x1.*.id]
}
output "output_local_variable" {
  value = local.all_null_resource
}

// 演示通过环境变量传递变量
// 通过命令行传递环境变量 TF_VAR_my_env_var1=xxx123 terraform apply
// 获取不通过环境变量根据提示输入变量值 terraform applys
// https://spacelift.io/blog/how-to-use-terraform-variables
variable "my_env_var1" {
  description = "测试使用的输入变量"
  type        = string
}
output "output_environment_variable" {
  value = var.my_env_var1
}


# 参数验证
variable "my_var2" {
  description = "用于演示参数验证的参数"
  type        = string
  validation {
    condition     = var.my_var2 != ""
    error_message = "没有指定参数my_var2"
  }
}

variable "my_var3" {
  description = "用于演示参数验证的参数"
  type        = string
  validation {
    condition     = length(var.my_var3) > 0 && length(var.my_var3) < 6
    error_message = "字符串长度在1-5之间"
  }
}

# 演示本地变量
# https://developer.hashicorp.com/terraform/language/values/locals
locals {
  my_local_var1 = "var1"
}

output "output_demo_local_variable" {
  value = "my_local_var1=${local.my_local_var1}"
}
