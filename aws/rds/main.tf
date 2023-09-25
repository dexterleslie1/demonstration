// https://aws.amazon.com/rds/?nc=sn&loc=3&dn=1

variable "my_aws_region" {
  type    = string
  default = "us-west-1"
}

provider "aws" {
  region = var.my_aws_region
}

// https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/db_instance
resource "aws_db_instance" "demo_db_mysql" {
  allocated_storage    = 10
  db_name              = "mydb"
  engine               = "mysql"
  engine_version       = "5.7"
  instance_class       = "db.t3.micro"
  username             = "foo"
  password             = "foobarbaz"
  parameter_group_name = "default.mysql5.7"
  skip_final_snapshot  = true
}
