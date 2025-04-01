package com.future.demo.job;

import com.future.demo.mapper.OrderMapper;
import com.future.demo.util.BatchInsertionUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SampleXxlJob {
    final static String StrConstPath = "/my-path1";

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    SqlSessionFactory sqlSessionFactory;
    @Autowired
    CuratorFramework curatorFramework;

    /**
     * 2、分片广播任务
     */
    @XxlJob("shardingJobHandler")
    public void shardingJobHandler() throws Exception {
        String jobParam = XxlJobHelper.getJobParam();
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        XxlJobHelper.log("分片参数：当前分片序号 = {}, 总分片数 = {}, 任务参数 = {}", shardIndex, shardTotal, jobParam);
        log.info("分片参数：当前分片序号 = {}, 总分片数 = {}, 任务参数 = {}", shardIndex, shardTotal, jobParam);

        DistributedDoubleBarrier barrier = null;
        try {
            // 插入数据记录总数
            int totalCount = 10000 * 100 / shardTotal;
            if (StringUtils.hasText(jobParam)) {
                int intTemporary = Integer.parseInt(jobParam);
                if (intTemporary < 10000) {
                    intTemporary = 10000;
                }
                totalCount = intTemporary / shardTotal;
            }

            this.orderMapper.truncate();

            barrier = new DistributedDoubleBarrier(curatorFramework, StrConstPath, shardTotal);
            barrier.enter(1, TimeUnit.MINUTES);

            BatchInsertionUtil.batchInsert(totalCount, orderMapper, sqlSessionFactory, log);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            if (barrier != null) {
                barrier.leave(1, TimeUnit.MINUTES);

                int totalCountInDB = orderMapper.count();
                log.info("数据库中总记录数 {}", totalCountInDB);
            }
        }

    }
}
