package com.future.demo.provider.service;

import com.future.demo.common.entity.BpkcMxb;
import com.future.demo.provider.mapper.BpkcMxbMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

@Slf4j
@Service
public class BpkcMxbService {
    private static final int BATCH_SIZE = 128;

    @Resource
    private BpkcMxbMapper bpkcMxbMapper;

    /** 应用启动时加载的全部 id（有序），用于随机选连续区间起点 */
    private long[] allIds = new long[0];

    @PostConstruct
    public void loadAllIds() {
        List<Long> ids = this.bpkcMxbMapper.selectAllIds();
        long[] arr = new long[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            arr[i] = ids.get(i);
        }
        this.allIds = arr;
        log.info("已加载 bpkc_mxb 全部 id，共 {} 条", this.allIds.length);
    }

    /**
     * id 连续：随机选起点，取长度为 limit 的区间；一条 SQL + Cursor 流式读，
     * 攒满一批交给 consumer。必须在事务内消费完 Cursor。
     */
    @Transactional(readOnly = true)
    public void streamRandomList(int limit, Consumer<List<BpkcMxb>> consumer) throws IOException {
        if (limit <= 0 || this.allIds.length == 0) {
            return;
        }
        int k = Math.min(limit, this.allIds.length);
        long startId = this.sampleStartId(k);
        try (Cursor<BpkcMxb> cursor = this.bpkcMxbMapper.streamFromId(startId, k)) {
            List<BpkcMxb> batch = new ArrayList<>(BATCH_SIZE);
            for (BpkcMxb row : cursor) {
                batch.add(row);
                if (batch.size() >= BATCH_SIZE) {
                    consumer.accept(new ArrayList<>(batch));
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                consumer.accept(new ArrayList<>(batch));
            }
        }
    }

    /**
     * 在有序 allIds 上随机选下标区间 [i, i+k)，返回起点 id，保证后续 LIMIT k 能取满。
     */
    private long sampleStartId(int k) {
        int n = this.allIds.length;
        if (k >= n) {
            return this.allIds[0];
        }
        int startIndex = ThreadLocalRandom.current().nextInt(n - k + 1);
        return this.allIds[startIndex];
    }
}
