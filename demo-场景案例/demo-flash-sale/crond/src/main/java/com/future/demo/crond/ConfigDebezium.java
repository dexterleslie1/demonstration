package com.future.demo.crond;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.demo.constant.Const;
import com.future.demo.dto.IncreaseCountDTO;
import com.future.demo.dto.RandomIdPickerAddIdEventDTO;
import io.debezium.data.Envelope;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Configuration
public class ConfigDebezium {

    @Value("${common_db_host:localhost}")
    private String dbHost;
    @Value("${common_db_port:3306}")
    private int dbPort;
    @Value("${debezium_offset_storage_file_filename:}")
    private String debeziumOffsetStorageFileFilename;
    @Value("${debezium_schema_history_internal_file_filename:}")
    private String debeziumSchemaHistoryInternalFileFilename;

    @Resource
    KafkaTemplate kafkaTemplate;
    @Resource
    ObjectMapper objectMapper;

    private final Executor executor = Executors.newFixedThreadPool(8);
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
        try {
            SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();
            Struct sourceRecordChangeValue = (Struct) sourceRecord.value();
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

                if (operation == Envelope.Operation.CREATE && "t_order".equals(tableName)) {
                    // 新增订单
                    Long orderId = (Long) payload.get("id");
                    IncreaseCountDTO increaseCountDTO = new IncreaseCountDTO(String.valueOf(orderId), "order");
                    /*increaseCountDTO.setType(IncreaseCountDTO.Type.MySQL);*/
                    increaseCountDTO.setCount(1);
                    String JSON = this.objectMapper.writeValueAsString(increaseCountDTO);
                    kafkaTemplate.send(Const.TopicIncreaseCount, JSON).get();

                    // 发出向随机 id 选择器添加 id 事件消息
                    RandomIdPickerAddIdEventDTO dto = new RandomIdPickerAddIdEventDTO();
                    dto.setFlag("order");
                    dto.setId(orderId);
                    JSON = objectMapper.writeValueAsString(dto);
                    kafkaTemplate.send(Const.TopicRandomIdPickerAddIdList, JSON).get();
                } else if (operation == Envelope.Operation.CREATE && "t_product".equals(tableName)) {
                    // 新增普通或者秒杀商品
                    Long productId = (Long) payload.get("id");
                    IncreaseCountDTO increaseCountDTO = new IncreaseCountDTO(String.valueOf(productId), "product");
                    /*increaseCountDTO.setType(IncreaseCountDTO.Type.MySQL);*/
                    increaseCountDTO.setCount(1);
                    String JSON = this.objectMapper.writeValueAsString(increaseCountDTO);
                    kafkaTemplate.send(Const.TopicIncreaseCount, JSON).get();

                    // 发出向随机 id 选择器添加 id 事件消息
                    RandomIdPickerAddIdEventDTO dto = new RandomIdPickerAddIdEventDTO();
                    dto.setFlag("product");
                    dto.setId(productId);
                    JSON = objectMapper.writeValueAsString(dto);
                    kafkaTemplate.send(Const.TopicRandomIdPickerAddIdList, JSON).get();
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public io.debezium.config.Configuration customerConnector() {
        return io.debezium.config.Configuration.create()
                .with("name", "customer-mysql-connector")
                .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", StringUtils.isBlank(debeziumOffsetStorageFileFilename) ? "/tmp/offset-" + UUID.randomUUID() + ".dat" : debeziumOffsetStorageFileFilename)
                .with("offset.flush.interval.ms", "10000")
                .with("database.hostname", dbHost)
                .with("database.port", dbPort)
                .with("database.user", "root")
                .with("database.password", "123456")
                .with("database.dbname", "demo")
                .with("database.include.list", "demo")
                .with("include.schema.changes", "false")
                .with("database.server.id", "10181")
                .with("database.server.name", "customer-mysql-db-server")

                // debezium2.x配置
                .with("schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory")
                .with("schema.history.internal.file.filename", StringUtils.isBlank(debeziumSchemaHistoryInternalFileFilename) ? "/tmp/dbhistory-" + UUID.randomUUID() + ".dat" : debeziumSchemaHistoryInternalFileFilename)

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
