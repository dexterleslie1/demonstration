package com.future.demo.doris;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.doris.flink.cfg.DorisExecutionOptions;
import org.apache.doris.flink.cfg.DorisOptions;
import org.apache.doris.flink.cfg.DorisReadOptions;
import org.apache.doris.flink.exception.DorisRuntimeException;
import org.apache.doris.flink.rest.RestService;
import org.apache.doris.flink.sink.BackendUtil;
import org.apache.doris.flink.sink.HttpPutBuilder;
import org.apache.doris.flink.sink.HttpUtil;
import org.apache.doris.flink.sink.writer.LabelGenerator;
import org.apache.doris.flink.sink.writer.LoadConstants;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Doris Flink Connector Stream Load 能力的批量写入器
 * （从 cloud-cloth-finace DorisStreamLoadWriter 移植，去掉 FlinkJobConfig 依赖）。
 */
public final class DorisStreamLoadWriter<T> {

    private static final Logger LOG = LoggerFactory.getLogger(DorisStreamLoadWriter.class);
    private static final String LOAD_URL_PATTERN = "http://%s/api/%s/%s/_stream_load";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
    private static final int MAX_RETRIES = 3;

    private final DorisOptions dorisOptions;
    private final DorisReadOptions readOptions;
    private final Properties streamLoadProp;
    private final LabelGenerator labelGenerator;
    private final BackendUtil backendUtil;
    private final HttpClientBuilder httpClientBuilder;
    private final String database;
    private final String table;
    private final int subtaskId;
    private final int bufferMaxRows;
    private final long flushIntervalMs;
    private final Object bufferLock = new Object();

    private List<StreamLoadRecord<T>> buffer;
    private ScheduledExecutorService flushScheduler;

    public DorisStreamLoadWriter(
            DorisStreamLoadConfig config,
            String labelPrefix,
            int subtaskId,
            int bufferMaxRows,
            long flushIntervalMs) {
        this.subtaskId = subtaskId;
        this.bufferMaxRows = bufferMaxRows;
        this.flushIntervalMs = flushIntervalMs;
        this.dorisOptions = DorisOptions.builder()
                .setFenodes(config.getFenodes())
                .setUsername(config.getUsername())
                .setPassword(config.getPassword())
                .setTableIdentifier(config.getTableIdentifier())
                .build();
        this.readOptions = DorisReadOptions.defaults();
        this.streamLoadProp = DorisExecutionOptions.defaultsProperties();
        this.labelGenerator = new LabelGenerator(labelPrefix, false, subtaskId);
        this.backendUtil = new BackendUtil(
                RestService.getBackendsV2(dorisOptions, readOptions, LOG));
        this.httpClientBuilder = new HttpUtil(readOptions, true).getHttpClientBuilderForBatch();

        String[] tableParts = config.getTableIdentifier().split("\\.", 2);
        if (tableParts.length != 2) {
            throw new IllegalArgumentException("Invalid Doris table identifier: " + config.getTableIdentifier());
        }
        this.database = tableParts[0];
        this.table = tableParts[1];
    }

    public void startBuffering(String schedulerThreadName) {
        buffer = new ArrayList<>(bufferMaxRows);
        flushScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, schedulerThreadName);
            thread.setDaemon(true);
            return thread;
        });
        flushScheduler.scheduleAtFixedRate(
                this::flushOnSchedule,
                flushIntervalMs,
                flushIntervalMs,
                TimeUnit.MILLISECONDS);
    }

    public void add(StreamLoadRecord<T> record) {
        synchronized (bufferLock) {
            buffer.add(record);
        }
    }

    public void addUpserts(List<T> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        synchronized (bufferLock) {
            for (T row : rows) {
                buffer.add(StreamLoadRecord.upsert(row));
            }
            flushIfFullLocked();
        }
    }

    public void addDeletes(List<T> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        synchronized (bufferLock) {
            for (T key : keys) {
                buffer.add(StreamLoadRecord.delete(key));
            }
            flushIfFullLocked();
        }
    }

    public void flushIfFull() {
        synchronized (bufferLock) {
            flushIfFullLocked();
        }
    }

    public void closeBuffering() throws InterruptedException {
        if (flushScheduler != null) {
            flushScheduler.shutdown();
            flushScheduler.awaitTermination(5, TimeUnit.SECONDS);
            flushScheduler = null;
        }
        synchronized (bufferLock) {
            flushBufferLocked();
            buffer = null;
        }
    }

    private void flushIfFullLocked() {
        if (buffer.size() >= bufferMaxRows) {
            flushBufferLocked();
        }
    }

    private void flushOnSchedule() {
        synchronized (bufferLock) {
            if (buffer != null && !buffer.isEmpty()) {
                flushBufferLocked();
            }
        }
    }

    private void flushBufferLocked() {
        if (buffer == null || buffer.isEmpty()) {
            return;
        }
        List<StreamLoadRecord<T>> pendingRecords = new ArrayList<>(buffer);
        buffer.clear();
        try {
            load(pendingRecords);
        } catch (IOException e) {
            throw new RuntimeException("Failed to stream load rows into Doris table " + database + "." + table, e);
        }
    }

    public void load(List<StreamLoadRecord<T>> records) throws IOException {
        if (records.isEmpty()) {
            return;
        }

        // LOG.info("Doris stream load {} rows into {}.{}", records.size(), database, table);

        boolean hasDelete = false;
        for (StreamLoadRecord<T> record : records) {
            if (record.isDelete()) {
                hasDelete = true;
                break;
            }
        }

        String body = toJsonLines(records);
        String label = labelGenerator.generateBatchLabel(table);
        String loadUrl = buildLoadUrl();
        Throwable lastError = null;

        for (int retry = 0; retry <= MAX_RETRIES; retry++) {
            String requestLabel = retry == 0 ? label : label + "_" + retry;
            HttpPutBuilder putBuilder = new HttpPutBuilder();
            putBuilder.setUrl(loadUrl)
                    .baseAuth(dorisOptions.getUsername(), dorisOptions.getPassword())
                    .setLabel(requestLabel)
                    .addCommonHeader()
                    .setEntity(new StringEntity(body, StandardCharsets.UTF_8))
                    .addProperties(streamLoadProp);
            if (hasDelete) {
                putBuilder.addHiddenColumns(true);
            }

            try (CloseableHttpClient httpClient = httpClientBuilder.build();
                 CloseableHttpResponse response = httpClient.execute(putBuilder.build())) {
                int statusCode = response.getStatusLine().getStatusCode();
                String reason = response.getStatusLine().toString();
                if (statusCode == 200 && response.getEntity() != null) {
                    String loadResult = EntityUtils.toString(response.getEntity());
                    if (isSuccessResponse(loadResult)) {
                        return;
                    }
                    lastError = new DorisRuntimeException(buildErrorMessage(loadResult));
                } else {
                    LOG.warn("Doris stream load failed with {}, reason {}", loadUrl, reason);
                    lastError = new DorisRuntimeException("stream load failed with: " + reason);
                }
            } catch (Exception ex) {
                lastError = ex;
                LOG.warn("Doris stream load error with {}, retry {}", loadUrl, retry, ex);
            }

            if (retry < MAX_RETRIES) {
                loadUrl = buildLoadUrl();
                sleepBeforeRetry(retry + 1);
            }
        }

        throw new IOException("Doris stream load failed after retries", lastError);
    }

    private String buildLoadUrl() {
        String hostPort = backendUtil.getAvailableBackend(subtaskId);
        return String.format(LOAD_URL_PATTERN, hostPort, database, table);
    }

    private static <T> String toJsonLines(List<StreamLoadRecord<T>> records) throws IOException {
        StringBuilder body = new StringBuilder(records.size() * 256);
        for (StreamLoadRecord<T> record : records) {
            ObjectNode node = OBJECT_MAPPER.valueToTree(record.getRow());
            if (record.isDelete()) {
                node.put(LoadConstants.DORIS_DELETE_SIGN, 1);
            }
            body.append(OBJECT_MAPPER.writeValueAsString(node)).append('\n');
        }
        return body.toString();
    }

    private static boolean isSuccessResponse(String loadResult) throws IOException {
        JsonNode root = OBJECT_MAPPER.readTree(loadResult);
        String status = readTextField(root, "Status", "status");
        return status != null && LoadConstants.DORIS_SUCCESS_STATUS.contains(status);
    }

    private static String buildErrorMessage(String loadResult) throws IOException {
        JsonNode root = OBJECT_MAPPER.readTree(loadResult);
        String message = readTextField(root, "Message", "message");
        String errorUrl = readTextField(root, "ErrorURL", "errorURL");
        return String.format("stream load error: %s, see more in %s", message, errorUrl);
    }

    private static String readTextField(JsonNode root, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode node = root.get(fieldName);
            if (node != null && !node.isNull()) {
                return node.asText();
            }
        }
        return null;
    }

    private static void sleepBeforeRetry(int retry) throws IOException {
        try {
            Thread.sleep(retry * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while retrying Doris stream load", e);
        }
    }
}
