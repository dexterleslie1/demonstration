module "say_hello" {
  # 引用github上的say_hello模块
  source = "github.com/dexterleslie1/demo-terraform-publish-self-defined-module.git"
  
  # 引用本地的say_hello模块
  # source = "./src"

  name = "Dexter"
}

# 引用terraform注册表上的say_hello模块
# module "say_hello" {
#   source  = "dexterleslie1/publish/demo"
#   version = "1.0.0"

#   name = "Dexter"
# }

output "output_for_debugging" {
  value = module.say_hello.hello_string
}

