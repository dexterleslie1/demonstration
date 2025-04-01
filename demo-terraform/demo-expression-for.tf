# 演示for表达式用法
# https://developer.hashicorp.com/terraform/language/expressions/for

output "output_demo_expression_for" {
  value = [for v in ["a", "b", "c"] : upper(v)]
}

