resource "random_password" "password" {
  length  = 16
  special = false
}

resource "aws_db_subnet_group" "default" {
  name       = "${var.my_project}-db-subnet-group"
  subnet_ids = var.my_subnet_ids


  tags = {
    Name = "${var.my_project}-db-subnet-group"
  }
}

resource "aws_db_instance" "database" {
  allocated_storage      = 10
  engine                 = "mysql"
  engine_version         = "8.0"
  instance_class         = "db.t2.micro"
  identifier             = "${var.my_project}-db-instance"
  db_name                = "pets"
  username               = "admin"
  password               = random_password.password.result
  db_subnet_group_name   = aws_db_subnet_group.default.name
  vpc_security_group_ids = var.my_security_group_ids
  skip_final_snapshot    = true
}
