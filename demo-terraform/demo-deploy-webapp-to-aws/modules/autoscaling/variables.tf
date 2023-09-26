variable "my_project" {
  type = string
}

variable "my_subnet_ids" {
  type = list(string)
}

variable "my_security_group_ids" {
  type = list(string)
}

variable "my_vpc_id" {
  type = string
}

variable "my_db_config" {
  type = object(
    {
      user     = string
      password = string
      database = string
      hostname = string
      port     = string
    }
  )
}
