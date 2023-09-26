// 演示template provider用法
// https://registry.terraform.io/providers/hashicorp/template/latest/docs/data-sources/file

// 演示template_file用法
data "template_file" "my_file" {
    template = "${file("${path.module}/config.tftpl")}"
    vars = {
        config_var1 = "hello"
        config_var2 = "world"
    }
}
output "template_file_output" {
    value = data.template_file.my_file.rendered
}
