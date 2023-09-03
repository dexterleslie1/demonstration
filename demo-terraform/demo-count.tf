// https://developer.hashicorp.com/terraform/language/meta-arguments/count
resource "null_resource" "x2" {
  count = 3
  triggers = {
    // 因为这个属性每次运行都变化，所以每次执行apply都会运行这个null_resource
    always_run = "${timestamp()}"
  }
  provisioner "local-exec" {
    command = "echo ++++++++++++ ${count.index}"
  }
}
