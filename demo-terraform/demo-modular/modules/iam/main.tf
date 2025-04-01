variable "iam_user_name" {
  type = string
}

variable "iam_user_policies" {
  type = list(string)
}

resource "aws_iam_user" "user" {
    name = var.iam_user_name
    force_destroy = true
}
resource "aws_iam_user_policy" "policy" {
    count = length(var.iam_user_policies)
    user = aws_iam_user.user.name
#     policy = <<-EOF
#     {
#         "Version": "2012-10-17",
#         "Statement": [
#             {
#                 "Action": [
#                     "ec2:Describe"
#                 ],
#                 "Effect": "Allow",
#                 "Resource": "*"
#             }
#         ]
#     }
# EOF
    policy = var.iam_user_policies[count.index]
}
resource "aws_iam_access_key" "access_key" {
    user = aws_iam_user.user.name
}

output "iam_user_name" {
    value = var.iam_user_name
}
output "credentails" {
    value = <<-EOF
    [${aws_iam_user.user.name}]
    aws_access_key_id = ${aws_iam_user.user.id}
    aws_secret_access_key = ${aws_iam_access_key.access_key.secret}
EOF
}