package com.future.demo;

import com.future.demo.benchmark.DdCompany751StyleGenerator;
import com.future.demo.doris.DorisStreamLoadConfig;
import com.future.demo.doris.DorisStreamLoadWriter;
import com.future.demo.doris.StreamLoadRecord;
import com.future.demo.entity.Dd;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 按 192.168.1.72 公司 751 的 dd 表规律，向本地 demot.dd 生成并 Stream Load 100 万条数据，
 * 供后续性能测试使用。
 * <p>
 * 需本地 demo-doris FE 9030 / HTTP 8030 可用。
 * </p>
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class DdGenerate100kTests {

    private static final int TOTAL = 1_000_000;
    private static final int STREAM_LOAD_BATCH = 5_000;
    private static final long COMPANY_ID = DdCompany751StyleGenerator.DEFAULT_COMPANY_ID;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Test
    public void generateAndLoad1mLikeCompany751() throws Exception {
        int deleted = jdbcTemplate.update("DELETE FROM dd WHERE company_id = ?", COMPANY_ID);
        log.info("cleared company_id={}, deletedRows={}", COMPANY_ID, deleted);

        DdCompany751StyleGenerator generator = DdCompany751StyleGenerator.forCompany751();
        DorisStreamLoadWriter<Dd> writer = new DorisStreamLoadWriter<>(
                DorisStreamLoadConfig.demoDefaults(),
                "gen_751_style_1m",
                0,
                STREAM_LOAD_BATCH,
                60_000L);

        long t0 = System.currentTimeMillis();
        int loaded = 0;
        List<StreamLoadRecord<Dd>> batch = new ArrayList<>(STREAM_LOAD_BATCH);
        for (int i = 0; i < TOTAL; i++) {
            batch.add(StreamLoadRecord.upsert(generator.next()));
            if (batch.size() >= STREAM_LOAD_BATCH) {
                writer.load(batch);
                loaded += batch.size();
                log.info("stream load progress {}/{}", loaded, TOTAL);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            writer.load(batch);
            loaded += batch.size();
        }
        long costMs = System.currentTimeMillis() - t0;

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM dd WHERE company_id = ?", Integer.class, COMPANY_ID);
        Assert.assertNotNull(count);
        Assert.assertEquals(TOTAL, count.intValue());

        List<Map<String, Object>> typeDist = jdbcTemplate.queryForList(
                "SELECT dj_type, dj_type_sub, COUNT(*) c FROM dd WHERE company_id = ? "
                        + "GROUP BY dj_type, dj_type_sub ORDER BY c DESC LIMIT 10",
                COMPANY_ID);
        log.info("generateAndLoad1m done: rows={}, costMs={}, topTypes={}", loaded, costMs, typeDist);
    }
}
