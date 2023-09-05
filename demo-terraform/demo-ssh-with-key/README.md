## 生成rsa钥匙对

```
# 生成私钥
openssl genrsa -out private.key 1024

# 生产OPENSSH格式公钥
ssh-keygen -f private.key -y > public.key
```

