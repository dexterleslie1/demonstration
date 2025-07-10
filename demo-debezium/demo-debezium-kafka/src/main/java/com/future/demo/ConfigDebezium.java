package com.future.demo;

import io.debezium.data.Envelope;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Configuration
public class ConfigDebezium {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;

    @PostConstruct
    public void init() {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(customerConnector().asProperties())
                .notifying(this::handleChangeEvent)
                .build();
        this.executor.execute(debeziumEngine);
    }

    @PreDestroy
    public void stop() throws IOException {
        if (this.debeziumEngine != null) {
            this.debeziumEngine.close();
        }
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();
        Struct sourceRecordChangeValue = (Struct) sourceRecord.value();
        if (sourceRecordChangeValue != null) {
            Envelope.Operation operation = Envelope.Operation.forCode((String) sourceRecordChangeValue.get("op"));
            String record = operation == Envelope.Operation.DELETE ? "before" : "after";
            Struct struct = (Struct) sourceRecordChangeValue.get(record);
            Map<String, Object> payload = struct.schema().fields().stream()
                    .map(Field::name)
                    .filter(fieldName -> struct.get(fieldName) != null)
                    .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                    .collect(toMap(Pair::getKey, Pair::getValue));

            log.info("payload={},operation={}", payload, operation);
        }
    }

    public io.debezium.config.Configuration customerConnector() {
        String randomString = UUID.randomUUID().toString();
        return io.debezium.config.Configuration.create()
                .with("name", "customer-mysql-connector")
                .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", "/tmp/offset-" + randomString + ".dat")
                .with("offset.flush.interval.ms", "10000")
                .with("database.hostname", "localhost")
                .with("database.port", "3306")
                .with("database.user", "root")
                .with("database.password", "123456")
                .with("database.dbname", "demo_db")
                .with("database.include.list", "demo_db")
                .with("include.schema.changes", "false")
                .with("database.server.id", "10181")
                .with("database.server.name", "customer-mysql-db-server")

                // debezium2.x配置
                .with("schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory")
                .with("schema.history.internal.file.filename", "/tmp/dbhistory-" + randomString + ".dat")

                // debezium1.x配置
                .with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
                .with("database.history.file.filename", "/tmp/dbhistory-" + randomString + ".dat")

                .with("errors.max.retries", "-1")
                .with("errors.retry.delay.initial.ms", "300")
                .with("errors.retry.delay.max.ms", "5000")
                .with("topic.prefix", "demo-debezium")
//                .with("schema.history.internal.kafka.topic", "demo-debezium")
//                .with("schema.history.internal.kafka.bootstrap.servers", "localhost:9092")
                .build();
    }

}
