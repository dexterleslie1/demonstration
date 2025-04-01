# 研究client_max_body_size、client_body_buffer_size等配置

## 参考资料

> [nginx 关于client_max_body_size client_body_buffer_size配置](https://www.cnblogs.com/feixiangmanon/p/10129018.html)

## 查看大body数据网络传输

```
iftop
```

## 编译和运行demo

```
# 编译镜像
sh build.sh

# 运行
docker-compose up

# 使用test-client项目模拟提交大body http请求
```