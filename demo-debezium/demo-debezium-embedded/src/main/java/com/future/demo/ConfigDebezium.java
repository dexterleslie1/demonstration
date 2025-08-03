package com.future.demo;

import io.debezium.data.Envelope;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Configuration
public class ConfigDebezium {

    private final Executor executor = Executors.newFixedThreadPool(8);
    private DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;

    private Consumer<List<RecordChangeEventWrapper>> callback = null;

    public void registerCallback(Consumer<List<RecordChangeEventWrapper>> callback) {
        this.callback = callback;
    }

    @Data
    public static class RecordChangeEventWrapper {
        RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent;
        String tableName;
        Map<String, Object> payload;
        Envelope.Operation operation;
    }

    @PostConstruct
    public void init() {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(customerConnector().asProperties())
                .notifying(this::handleBatch)
                .build();
        this.executor.execute(debeziumEngine);
    }

    @PreDestroy
    public void stop() throws IOException {
        if (this.debeziumEngine != null) {
            this.debeziumEngine.close();
        }
    }

    private void handleBatch(List<RecordChangeEvent<SourceRecord>> recordChangeEventList,
                             DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> committer) throws InterruptedException {
        log.info("recordChangeEventList {}", recordChangeEventList);

        List<RecordChangeEventWrapper> wrapperList = new ArrayList<>();
        for (RecordChangeEvent<SourceRecord> recordChangeEvent : recordChangeEventList) {
            committer.markProcessed(recordChangeEvent);

            SourceRecord sourceRecord = recordChangeEvent.record();
            Struct sourceRecordChangeValue = (Struct) sourceRecord.value();
            // 提示：在执行 delete from t_user where username=#{username} 时，有一个 sourceRecordChangeValue==null 的未知事件
            // 所以在此要作出判断
            if (sourceRecordChangeValue != null) {
                // 获取表名称
                String tableName = ((Struct) sourceRecordChangeValue.get("source")).getString("table");
                Envelope.Operation operation = Envelope.Operation.forCode((String) sourceRecordChangeValue.get("op"));
                String record = operation == Envelope.Operation.DELETE ? "before" : "after";
                Struct struct = (Struct) sourceRecordChangeValue.get(record);
                Map<String, Object> payload = struct.schema().fields().stream()
                        .map(Field::name)
                        .filter(fieldName -> struct.get(fieldName) != null)
                        .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                        .collect(toMap(Pair::getKey, Pair::getValue));

                // 输出样例：
                // 插入类型数据
                // table=t_user,payload={password=123456, createTime=1752076266000, id=3, username=user2},operation=CREATE
                // 修改类型数据
                // table=t_user,payload={password=123456, createTime=1752076266000, id=3, username=userx},operation=UPDATE
                // 删除类型数据
                // table=t_user,payload={password=123456, createTime=1752076266000, id=3, username=userx},operation=DELETE
                // 初始化数据库类型数据
                // table=t_user,payload={password=1, createTime=1752076266000, id=3, username=user2x},operation=READ
                // log.info("table={},payload={},operation={}", tableName, payload, operation);

                RecordChangeEventWrapper wrapper = new RecordChangeEventWrapper();
                wrapper.setSourceRecordRecordChangeEvent(recordChangeEvent);
                wrapper.setTableName(tableName);
                wrapper.setOperation(operation);
                wrapper.setPayload(payload);
                wrapperList.add(wrapper);
            }
        }
        committer.markBatchFinished();

        if (callback != null && !wrapperList.isEmpty()) {
            callback.accept(wrapperList);
        }
        /*boolean b = true;
        if (b) {
            throw new RuntimeException("测试错误");
        }*/
    }

    public io.debezium.config.Configuration customerConnector() {
        String uuidStr = UUID.randomUUID().toString();
        String offsetStorageFilename = "/tmp/offset-" + uuidStr + ".dat";
        String schemaHistoryFilename = "/tmp/dbhistory-" + uuidStr + ".dat";

        if (log.isInfoEnabled()) {
            log.info("Offset 信息存储文件 {}，Schema History 信息存储文件 {}", offsetStorageFilename, schemaHistoryFilename);
        }

        return io.debezium.config.Configuration.create()
                .with("name", "customer-mysql-connector")
                .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", offsetStorageFilename)
                .with("offset.flush.interval.ms", "10000")
                .with("database.hostname", "localhost")
                .with("database.port", "3306")
                .with("database.user", "root")
                .with("database.password", "123456")
                .with("database.dbname", "demo")
                .with("database.include.list", "demo")
                // 禁用 DDL 事件（仅捕获 DML）
                .with("include.schema.changes", "false")
                .with("database.server.id", "10181")
                .with("database.server.name", "customer-mysql-db-server")
                // 指定连接器从 MySQL 二进制日志（Binlog）中单次读取的最大事件数（即记录数）
                .with("max.batch.size", "10")

                // debezium2.x配置
                .with("schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory")
                .with("schema.history.internal.file.filename", schemaHistoryFilename)

                // debezium1.x配置
                /*.with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
                .with("database.history.file.filename", "/tmp/dbhistory-debezium.dat")*/

                .with("errors.max.retries", "-1")
                .with("errors.retry.delay.initial.ms", "300")
                .with("errors.retry.delay.max.ms", "5000")
                .with("topic.prefix", "demo-debezium")
//                .with("schema.history.internal.kafka.topic", "demo-debezium")
//                .with("schema.history.internal.kafka.bootstrap.servers", "localhost:9092")
                .build();
    }

}
