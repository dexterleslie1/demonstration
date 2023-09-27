//#region 演示null_resource的用法

// https://registry.terraform.io/providers/hashicorp/null/latest/docs

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

resource "null_resource" "test" {
        triggers = {
                // 因为这个属性每次运行都变化，所以每次执行apply都会运行这个null_resource
                always_run = "${timestamp()}"
        }
        provisioner "local-exec" {
                command = "date"
        }
}

//#endregion

//#region 演示null_data_source用法

// https://registry.terraform.io/providers/hashicorp/null/latest/docs/data-sources/data_source
data "null_data_source" "values" {
    inputs = {
        say_hello1: "Hello first!"
        say_hello2: "Hello second!"
    }
}
output "output_null_data_source" {
    value = {
        say1: data.null_data_source.values.outputs["say_hello1"]
        say2: data.null_data_source.values.outputs["say_hello2"]
    }
}

//#endregion
