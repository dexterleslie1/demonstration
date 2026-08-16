package com.future.demo;

import com.future.demo.benchmark.BpkcMxbQueryParam;
import com.future.demo.benchmark.DdCompany751StyleGenerator;
import com.future.demo.service.DdService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 冒烟：bpkcMxb 对齐 SQL（含随机参数）可在本地 Doris 执行。
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class DdBpkcMxbSmokeTests {

    private static final long COMPANY_ID = DdCompany751StyleGenerator.DEFAULT_COMPANY_ID;

    @Resource
    private DdService ddService;

    @Test
    public void queryBpkcMxbCompanyOnly() {
        BpkcMxbQueryParam param = BpkcMxbQueryParam.companyOnly(COMPANY_ID);
        long t0 = System.currentTimeMillis();
        List<Map<String, Object>> rows = ddService.queryBpkcMxb(param);
        long cost = System.currentTimeMillis() - t0;
        Assert.assertNotNull(rows);
        log.info("bpkcMxb companyOnly: rows={}, costMs={}, param={}", rows.size(), cost, param);
    }

    @Test
    public void queryBpkcMxbRandomParams() {
        for (int i = 0; i < 5; i++) {
            BpkcMxbQueryParam param = BpkcMxbQueryParam.random(COMPANY_ID);
            long t0 = System.currentTimeMillis();
            List<Map<String, Object>> rows = ddService.queryBpkcMxb(param);
            long cost = System.currentTimeMillis() - t0;
            Assert.assertNotNull(rows);
            log.info("bpkcMxb random[{}]: rows={}, costMs={}, param={}", i, rows.size(), cost, param);
        }
    }
}
