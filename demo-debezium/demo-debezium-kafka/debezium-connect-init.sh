#!/bin/sh

connector_host="debezium-connect"
json_data='{
             "name": "demo-connector",
             "config": {
               "connector.class": "io.debezium.connector.mysql.MySqlConnector",
               "tasks.max": "1",
               "database.hostname": "db",
               "database.port": "3306",
               "database.user": "root",
               "database.password": "123456",
               "database.server.id": "184054",
               "topic.prefix": "demo-debezium",
               "database.include.list": "demo",
               "schema.history.internal.kafka.bootstrap.servers": "kafka:9092",
               "schema.history.internal.kafka.topic": "schemahistory.demo"
             }
           }'
#"max.batch.size": 8190,
#"poll.interval.ms": 100

result=$(
    curl -f -o /dev/null -X POST -H "Accept:application/json" -H "Content-Type:application/json" \
        --connect-timeout 5 $connector_host:8083/connectors/ \
        -d "$json_data" \
        2>&1
)
exit_code=$?
# echo $result
# echo $exit_code

# 错误信息中是否包含 409，如果包含表示之前已经初始化，不需要重试
echo "$result" | grep -q "409"
contains409=$?
# echo $contains409

while [ ! $exit_code -eq 0 ] && [ ! $contains409 -eq 0 ];
do
    echo "`date` - curl执行失败重试";
    sleep 5;

    result=$(
        curl -f -o /dev/null -X POST -H "Accept:application/json" -H "Content-Type:application/json" \
            --connect-timeout 5 $connector_host:8083/connectors/ \
            -d "$json_data" \
            2>&1
    )
    exit_code=$?
    # echo $result

    echo "$result" | grep -q "409"
    contains409=$?
done

if [ ! $exit_code -eq 0 ] && [ $contains409 -eq 0 ]; then
    echo "`date` - 之前已经初始化过 debezium-connect"
else
    echo "`date` - 成功初始化 debezium-connect"
fi
