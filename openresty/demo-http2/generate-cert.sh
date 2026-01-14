#!/bin/bash
echo "生成自签名SSL证书..."
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout key.pem \
    -out cert.crt \
    -subj "/C=CN/ST=State/L=City/O=Organization/CN=localhost"

echo "证书生成完成！"
echo "cert.crt 和 key.pem 已创建"

