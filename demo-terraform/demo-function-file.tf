// 使用file函数读取文件内容
output "myfile1" {
    value = file("${path.module}/for-testing-no-removing/1.log")
}