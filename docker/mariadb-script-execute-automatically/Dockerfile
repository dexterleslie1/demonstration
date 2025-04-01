from mariadb:10.4.19

run apt-get update
run apt-get install -y curl

env DOCKERIZE_VERSION v0.6.1
run curl --silent --output dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz https://bucketxyh.oss-cn-hongkong.aliyuncs.com/docker/dockerize-linux-amd64-v0.6.1.tar.gz
run tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

copy test.sql /docker-entrypoint-initdb.d/
copy mysql.cnf /etc/mysql/conf.d/my.cnf

