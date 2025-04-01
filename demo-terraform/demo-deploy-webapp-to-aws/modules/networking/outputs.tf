output "vpc_id" {
    value = aws_vpc.demo_vpc.id
}

output "security_group_ids" {
    value = [aws_security_group.demo_security_group.id]
}

output "subnet_ids" {
    value = [aws_subnet.demo_subnet.id, aws_subnet.demo_subnet1.id]
}