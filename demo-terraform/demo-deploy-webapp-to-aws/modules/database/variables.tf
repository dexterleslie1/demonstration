variable "my_project" {
  type = string
}

variable "my_subnet_ids" {
  type =  list(string)
}

variable "my_security_group_ids" {
  type = list(string)
}