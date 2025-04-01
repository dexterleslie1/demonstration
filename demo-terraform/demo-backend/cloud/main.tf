terraform {
  cloud {
    organization = "future-demo-my-org"

    workspaces {
      # 指定使用prod workspace，不能使用terraform workspace select切换workspace
      # name = "prod"

      # 设置拥有指定tag的workspace，可以使用terraform workspace select命令切换workspace
      tags = ["my-tag"]
    }
  }
}

resource "null_resource" "demo_null_resource" {
  triggers = {
    always = timestamp()
  }

  provisioner "local-exec" {
    command = "echo 'Hello world!'"
  }
}
