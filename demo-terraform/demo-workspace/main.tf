variable "param1" {
  type = string
}

output "output_current_workspace" {
  value = "${terraform.workspace} - ${var.param1}"
}
