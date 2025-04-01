variable "my_aws_region" {
  type    = string
  default = "us-west-1"
}
variable "my_tag" {
  type = string
  default = "demo-terraform-backend"
}

provider "aws" {
  region = var.my_aws_region
}

resource "aws_resourcegroups_group" "demo_aws_resourcegroups_group" {
    name = "demo-aws-resource-groups-group1"

    resource_query {
      query = <<-JSON
      {
  "ResourceTypeFilters": [
    "AWS::AllSupported"
  ],
  "TagFilters": [
    {
      "Key": "ResourceGroup",
      "Values": ["${var.my_tag}"]
    }
  ]
}
JSON
    }
}

resource "aws_s3_bucket" "demo_aws_s3_bucket1" {
  bucket = "demo-aws-s3-bucket1"
  # 当bucket销毁时所有bucket内所有object都被一同销毁
  force_destroy = true

  tags = {
    ResourceGroup = "${var.my_tag}"
  }
}
resource "aws_s3_bucket_versioning" "demo_aws_s3_bucket_versioning1" {
  bucket = aws_s3_bucket.demo_aws_s3_bucket1.id

  versioning_configuration {
    status = "Enabled"
  }

#   tags = {
#     ResourceGroup = "${var.my_tag}"
#   }
}
resource "aws_s3_bucket_server_side_encryption_configuration" "demo_aws_s3_bucket_server_side_encryption_configuration1" {
  bucket = aws_s3_bucket.demo_aws_s3_bucket1.id

  rule {
    # 当使用 KMS 加密来加密此存储桶中的新对象时，存储桶密钥会减少对 AWS KMS 的调用，从而降低加密成本。
    bucket_key_enabled = true
  }

#   tags = {
#     ResourceGroup = "${var.my_tag}"
#   }
}
resource "aws_s3_bucket_public_access_block" "demo_aws_s3_bucket_public_access_block1" {
  bucket = aws_s3_bucket.demo_aws_s3_bucket1.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  # 启用阻止所有公开访问
  restrict_public_buckets = true

#   tags = {
#     ResourceGroup = "${var.my_tag}"
#   }
}

resource "aws_dynamodb_table" "demo_aws_dynamodb_table" {
  name = "demo-aws-dynamodb-table1"
  hash_key = "LockID"
  billing_mode = "PAY_PER_REQUEST"
  attribute {
    name = "LockID"
    type = "S"
  }

  tags = {
    ResourceGroup = "${var.my_tag}"
  }
}

output "config" {
    value = {
        bucket = aws_s3_bucket.demo_aws_s3_bucket1.bucket
        region = var.my_aws_region
        dynamodb_table = aws_dynamodb_table.demo_aws_dynamodb_table.name
    }
}

