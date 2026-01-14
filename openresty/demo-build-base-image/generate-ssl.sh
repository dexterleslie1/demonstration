#!/bin/bash

# 创建 SSL 证书目录
SSL_DIR="./ssl"
mkdir -p "$SSL_DIR"

# 生成私钥
openssl genrsa -out "$SSL_DIR/key.pem" 2048

# 生成自签名证书（有效期 365 天）
openssl req -new -x509 -key "$SSL_DIR/key.pem" -out "$SSL_DIR/cert.pem" -days 365 -subj "/C=CN/ST=State/L=City/O=Organization/CN=localhost"

echo "SSL 证书已生成到 $SSL_DIR 目录"
echo "证书文件: $SSL_DIR/cert.pem"
echo "私钥文件: $SSL_DIR/key.pem"

