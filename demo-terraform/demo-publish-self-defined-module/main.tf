module "say_hello" {
  source = "github.com/dexterleslie1/demo-terraform-publish-self-defined-module.git"
  # source = "./src"

  name = "Dexter"
}

output "output_for_debugging" {
  value = module.say_hello.hello_string
}

