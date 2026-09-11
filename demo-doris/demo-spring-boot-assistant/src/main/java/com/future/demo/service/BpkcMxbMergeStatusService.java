package com.future.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.future.demo.entity.BpkcMxbMergeStatus;
import com.future.demo.mapper.BpkcMxbMergeStatusMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * demot.bpkc_mxb_merge_status CRUD / 批量插入。
 */
@Service
public class BpkcMxbMergeStatusService extends ServiceImpl<BpkcMxbMergeStatusMapper, BpkcMxbMergeStatus> {

    /**
     * MyBatis-Plus 批量插入（ExecutorType.BATCH）。
     *
     * @param rows      待插入行
     * @param batchSize 每批条数
     */
    public boolean insertBatch(Collection<BpkcMxbMergeStatus> rows, int batchSize) {
        return saveBatch(rows, batchSize);
    }
}
