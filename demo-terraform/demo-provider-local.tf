// local provider用法
// https://registry.terraform.io/providers/hashicorp/local/latest/docs

provider "local" {
  
}

// 在本地创建.testing.txt文件
resource "local_file" "foo" {
  content  = "foo!"
  filename = "${path.module}/.testing.txt"
}

// 读取.testing.txt内容
data "local_file" "myfile1" {
  filename = "${path.module}/for-testing-no-removing/1.log"
}

output "myoutput1" {
  value = data.local_file.myfile1.content
}

