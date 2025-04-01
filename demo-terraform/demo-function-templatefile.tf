# 演示templatefile函数用法
# https://developer.hashicorp.com/terraform/language/functions/templatefile

output "output_demo_templatefile" {
  value = templatefile("./config.tftpl", {
    config_var1 = "v1",
    config_var2 = "v2",
  })
}

