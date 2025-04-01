variable "my_aws_region" {
  type    = string
  default = "us-west-1"
}
variable "username" {
  type    = string
  default = "foo"
}
variable "password" {
  type    = string
  default = "foobarbar"
}

provider "aws" {
  region = var.my_aws_region
}

resource "aws_db_instance" "demo_db_mysql" {
  allocated_storage    = 10
  db_name              = "mydb"
  engine               = "mysql"
  engine_version       = "5.7"
  instance_class       = "db.t3.micro"
  username             = var.username
  password             = var.password
  parameter_group_name = "default.mysql5.7"
  skip_final_snapshot  = true
}

data "aws_iam_policy_document" "assume_role" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role" "iam_for_lambda" {
  name               = "iam_for_lambda"
  assume_role_policy = data.aws_iam_policy_document.assume_role.json
}

resource "aws_lambda_function" "demo_lambda" {
  filename      = "code.zip"
  function_name = "demo-lambda"
  role          = aws_iam_role.iam_for_lambda.arn
  handler       = "exports.main"

  source_code_hash = filebase64sha256("code.zip")
  runtime          = "nodejs18.x"

  // NOTE: 演示使用环境变量意外地暴露帐号密码到lambda function控制台中
  environment {
    variables = {
      USERNAME = var.username
      PASSWORD = var.password
    }
  }
}
