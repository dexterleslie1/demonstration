package com.future.demo.controller;

import cn.hutool.core.util.RandomUtil;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.IndexMapper;
import com.future.demo.OrderIndexListByUserIdModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class DemoController {
    final static AtomicInteger IdCounter = new AtomicInteger();

    @Resource
    IndexMapper indexMapper;
    @Resource
    // cassandra3 驱动程序
    // Session session;
    // cassandra5 驱动程序
    CqlSession session;

    /**
     * 协助测试迁移时生成测试数据以检查是否丢失数据
     *
     * @return
     * @throws Exception
     */
    @GetMapping("testAssistMigrationGenerateDatum")
    public ObjectResponse<String> testAssistMigrationGenerateDatum() throws Exception {
        Long userId = RandomUtil.randomLong(1, Long.MAX_VALUE);
        OrderIndexListByUserIdModel model = new OrderIndexListByUserIdModel();
        model.setUserId(userId);
        model.setStatus("x");
        Instant now = Instant.now();
        model.setCreateTime(now.atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime());
        long ordeId = IdCounter.incrementAndGet();
        model.setOrderId(ordeId);
        this.indexMapper.insertBatchOrderIndexListByUserId(Collections.singletonList(model));

        int randomMilliseconds = RandomUtil.randomInt(1, 1000);
        TimeUnit.MILLISECONDS.sleep(randomMilliseconds);

        return ResponseUtils.successObject("成功调用");
    }

    /**
     * 获取测试迁移数据总数以检查是否丢失数据
     *
     * @return
     */
    @GetMapping("testAssistMigrationGetTotalCount")
    public ObjectResponse<Integer> testAssistMigrationGetTotalCount() {
        ResultSet resultSet = session.execute("select count(*) from t_order_list_by_userId");
        Long count = resultSet.one().getLong(0);
        return ResponseUtils.successObject(count == null ? 0 : count.intValue());
    }
}
