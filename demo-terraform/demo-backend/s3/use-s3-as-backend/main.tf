terraform {
  backend "s3" {
    bucket = "demo-aws-s3-bucket1"
    # s3存储对应的key
    key = "my-s3-key"
    region = "us-west-1"
    dynamodb_table = "demo-aws-dynamodb-table1"
  }
}

resource "null_resource" "motto" {
  triggers = {
    always = timestamp()
  }

  provisioner "local-exec" {
    command = "echo 'Hello world!'"
  }
}
