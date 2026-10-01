package com.future.demo;

import org.apache.flink.api.connector.sink2.Sink;
import org.apache.flink.api.connector.sink2.SinkWriter;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;

import java.io.IOException;

/**
 * 演示 parent_dim、child_dim、mx_dim 三张表分别订阅 MySQL CDC，并各自接入自定义 Sink。
 * <p>
 * 与 SQL {@code INSERT INTO print} 不同，本示例通过 {@code toChangelogStream} 转为 DataStream，
 * 再使用新版 {@link Sink} API 为每张表实现独立的写入逻辑（前缀、字段格式化等）。
 * <p>
 * 运行前需先启动 MySQL 并执行 db.sql 创建 parent_dim、child_dim、mx_dim 表。
 *
 * <pre>
 * insert into parent_dim(id, company_id) values(10011, 10);
 * insert into child_dim(id, company_id, parent_id) values(20011, 10, 10011);
 * insert into mx_dim(id, company_id, parent_id, child_id) values(30011, 10, 10011, 20011);
 *
 * delete from mx_dim where id = 30011;
 * delete from child_dim where id = 20011;
 * delete from parent_dim where id = 10011;
 * </pre>
 */
public class CustomizeSinkTests {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        System.out.println("=== 三表 CDC 分别订阅 + 自定义 Sink 演示 ===");

        createParentDimSource(tableEnv);
        createChildDimSource(tableEnv);
        createMxDimSource(tableEnv);

        Table parentDim = tableEnv.sqlQuery("SELECT id, company_id FROM parent_dim");
        Table childDim = tableEnv.sqlQuery("SELECT id, company_id, parent_id FROM child_dim");
        Table mxDim = tableEnv.sqlQuery("SELECT id, company_id, parent_id, child_id FROM mx_dim");

        tableEnv.toChangelogStream(parentDim)
                .sinkTo(new ParentDimCustomSink())
                .name("parent-dim-custom-sink");

        tableEnv.toChangelogStream(childDim)
                .sinkTo(new ChildDimCustomSink())
                .name("child-dim-custom-sink");

        tableEnv.toChangelogStream(mxDim)
                .sinkTo(new MxDimCustomSink())
                .name("mx-dim-custom-sink");

        env.execute("Customize Sink Tests");
    }

    private static void createParentDimSource(StreamTableEnvironment tableEnv) {
        tableEnv.executeSql(
                "CREATE TABLE parent_dim (\n" +
                        "    id BIGINT,\n" +
                        "    company_id BIGINT,\n" +
                        "    PRIMARY KEY (id) NOT ENFORCED\n" +
                        ") WITH (\n" +
                        "    'connector' = 'mysql-cdc',\n" +
                        "    'hostname' = 'localhost',\n" +
                        "    'port' = '3306',\n" +
                        "    'username' = 'root',\n" +
                        "    'password' = '123456',\n" +
                        "    'database-name' = 'demo',\n" +
                        "    'table-name' = 'parent_dim'\n" +
                        ")"
        );
    }

    private static void createChildDimSource(StreamTableEnvironment tableEnv) {
        tableEnv.executeSql(
                "CREATE TABLE child_dim (\n" +
                        "    id BIGINT,\n" +
                        "    company_id BIGINT,\n" +
                        "    parent_id BIGINT,\n" +
                        "    PRIMARY KEY (id) NOT ENFORCED\n" +
                        ") WITH (\n" +
                        "    'connector' = 'mysql-cdc',\n" +
                        "    'hostname' = 'localhost',\n" +
                        "    'port' = '3306',\n" +
                        "    'username' = 'root',\n" +
                        "    'password' = '123456',\n" +
                        "    'database-name' = 'demo',\n" +
                        "    'table-name' = 'child_dim'\n" +
                        ")"
        );
    }

    private static void createMxDimSource(StreamTableEnvironment tableEnv) {
        tableEnv.executeSql(
                "CREATE TABLE mx_dim (\n" +
                        "    id BIGINT,\n" +
                        "    company_id BIGINT,\n" +
                        "    parent_id BIGINT,\n" +
                        "    child_id BIGINT,\n" +
                        "    PRIMARY KEY (id) NOT ENFORCED\n" +
                        ") WITH (\n" +
                        "    'connector' = 'mysql-cdc',\n" +
                        "    'hostname' = 'localhost',\n" +
                        "    'port' = '3306',\n" +
                        "    'username' = 'root',\n" +
                        "    'password' = '123456',\n" +
                        "    'database-name' = 'demo',\n" +
                        "    'table-name' = 'mx_dim'\n" +
                        ")"
        );
    }

    private static String changelogKindLabel(RowKind kind) {
        switch (kind) {
            case INSERT:
                return "+I";
            case DELETE:
                return "-D";
            case UPDATE_BEFORE:
                return "-U";
            case UPDATE_AFTER:
                return "+U";
            default:
                return kind.toString();
        }
    }

    /**
     * parent_dim 自定义 Sink：只输出主表 id 与 company_id。
     */
    private static class ParentDimCustomSink implements Sink<Row> {

        @Override
        public SinkWriter<Row> createWriter(InitContext context) {
            return new ParentDimSinkWriter();
        }

        private static class ParentDimSinkWriter implements SinkWriter<Row> {

            @Override
            public void write(Row row, Context context) {
                System.out.printf(
                        "[parent_dim] %s id=%s, company_id=%s%n",
                        changelogKindLabel(row.getKind()),
                        row.getField(0),
                        row.getField(1)
                );
            }

            @Override
            public void flush(boolean endOfInput) {
            }

            @Override
            public void close() {
            }
        }
    }

    /**
     * child_dim 自定义 Sink：额外标注 parent_id，便于观察主子关系。
     */
    private static class ChildDimCustomSink implements Sink<Row> {

        @Override
        public SinkWriter<Row> createWriter(InitContext context) {
            return new ChildDimSinkWriter();
        }

        private static class ChildDimSinkWriter implements SinkWriter<Row> {

            @Override
            public void write(Row row, Context context) {
                System.out.printf(
                        "[child_dim] %s child_id=%s, company_id=%s, parent_id=%s%n",
                        changelogKindLabel(row.getKind()),
                        row.getField(0),
                        row.getField(1),
                        row.getField(2)
                );
            }

            @Override
            public void flush(boolean endOfInput) {
            }

            @Override
            public void close() {
            }
        }
    }

    /**
     * mx_dim 自定义 Sink：输出完整三级关联键。
     */
    private static class MxDimCustomSink implements Sink<Row> {

        @Override
        public SinkWriter<Row> createWriter(InitContext context) throws IOException {
            return new MxDimSinkWriter();
        }

        private static class MxDimSinkWriter implements SinkWriter<Row> {

            @Override
            public void write(Row row, Context context) {
                System.out.printf(
                        "[mx_dim] %s mx_id=%s, company_id=%s, parent_id=%s, child_id=%s%n",
                        changelogKindLabel(row.getKind()),
                        row.getField(0),
                        row.getField(1),
                        row.getField(2),
                        row.getField(3)
                );
            }

            @Override
            public void flush(boolean endOfInput) {
            }

            @Override
            public void close() {
            }
        }
    }
}
