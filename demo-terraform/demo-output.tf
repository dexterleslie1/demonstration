// 演示output用法
output "current_dir" {
    value = "${path.module}"
}

// 返回list类型output
resource "null_resource" "x_output_1" {
    count = 3
}
output "output_output_list" {
  value = [null_resource.x_output_1.*.id]
}
