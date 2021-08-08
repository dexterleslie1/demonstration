##### redis容器启动
docker run --rm -p 6379:6379 -e "TZ=Asia/Shanghai" redis:6.2.4 redis-server --requirepass 123456