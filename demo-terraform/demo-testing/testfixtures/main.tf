variable "my_aws_region" {
  type    = string
  default = "ap-northeast-1"
}
variable "my_aws_bucket_name" {
  type    = string
  default = "my-demo-bucket-dex1"
}

provider "aws" {
  region = var.my_aws_region
}

resource "aws_s3_bucket" "website" {
  bucket        = var.my_aws_bucket_name
  force_destroy = true

  website {
    index_document = "index.html"
  }
}

resource "aws_s3_bucket_ownership_controls" "example" {
  bucket = aws_s3_bucket.website.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_public_access_block" "example" {
  bucket = aws_s3_bucket.website.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}
resource "aws_s3_bucket_acl" "acl" {
  depends_on = [
    aws_s3_bucket_ownership_controls.example,
    aws_s3_bucket_public_access_block.example,
  ]

  bucket = aws_s3_bucket.website.id
  acl    = "public-read"
}

resource "aws_s3_bucket_policy" "policy" {
  depends_on = [aws_s3_bucket_acl.acl]
  bucket     = aws_s3_bucket.website.id
  policy     = <<-EOF
    {
        "Version": "2008-10-17",
        "Statement": [{
            "Sid": "PublicReadForGetBucketObjects",
            "Effect": "Allow",
            "Principal": {
                "AWS": "*"
            },
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::${var.my_aws_bucket_name}/*"
        }]
    }
EOF
}


resource "aws_s3_bucket_object" "bucket_object" {
  bucket       = aws_s3_bucket.website.bucket
  key          = "index.html"
  source       = "index.html"
  etag         = filemd5("${path.module}/index.html")
  content_type = "text/html"
}

output "endpoint" {
  value = aws_s3_bucket.website.website_endpoint
}

