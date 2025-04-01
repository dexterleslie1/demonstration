// 演示使用length函数获取list类型变量长度
variable "jmeter_salve_ips1" {
    type = list(string)
    default = [ "1", "2" ]
}

output "output_jmeter_salve_ips1_length" {
    value = length(var.jmeter_salve_ips1)
}

