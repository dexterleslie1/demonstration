variable "my_aws_region" {
  type    = string
  default = "us-west-1"
}

provider "aws" {
  region = var.my_aws_region
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

// https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lambda_function
resource "aws_lambda_function" "demo_lambda" {
  filename      = "code.zip"
  function_name = "demo-lambda"
  role          = aws_iam_role.iam_for_lambda.arn
  handler       = "exports.main"

  source_code_hash = filebase64sha256("code.zip")
  runtime          = "nodejs18.x"

  environment {
    variables = {
      USERNAME = "foo"
      PASSWORD = "foobar"
    }
  }
}
