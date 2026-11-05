package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@Slf4j
public class ApplicationTests {

    private static final int TOTAL_COUNT = 200000;
    private static final int CONCURRENT_THREADS = 8;

    @Resource
    JdbcTemplate jdbcTemplate;

    private String jsonContent;

    @Before
    public void setUp() throws Exception {
        try (InputStream inputStream = new ClassPathResource("sample-json.json").getInputStream()) {
            jsonContent = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }

    /**
     * 并发往未压缩表、压缩表各插入 10 万条相同 JSON，便于对比磁盘占用
     */
    @Test
    public void testInsertMediumTextJson() throws InterruptedException {
        insertConcurrently("t_mediumtext");
        insertConcurrently("t_mediumtext_compressed");
    }

    private void insertConcurrently(String tableName) throws InterruptedException {
        log.info("清空表 {}", tableName);
        jdbcTemplate.execute("truncate table " + tableName);

        int batchCount = TOTAL_COUNT / CONCURRENT_THREADS;
        AtomicInteger progress = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        String sql = "insert into " + tableName + "(content) values(?)";
        long start = System.currentTimeMillis();
        log.info("开始向 {} 插入 {} 条数据，并发线程={}", tableName, TOTAL_COUNT, CONCURRENT_THREADS);

        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < batchCount; j++) {
                    jdbcTemplate.update(sql, jsonContent);
                    int done = progress.incrementAndGet();
                    if (done % 10000 == 0) {
                        log.info("{} 已插入 {} 条", tableName, done);
                    }
                }
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
        }

        long costMs = System.currentTimeMillis() - start;
        Integer count = jdbcTemplate.queryForObject("select count(*) from " + tableName, Integer.class);
        log.info("{} 插入完成，count={}，耗时 {} ms", tableName, count, costMs);
    }
}
