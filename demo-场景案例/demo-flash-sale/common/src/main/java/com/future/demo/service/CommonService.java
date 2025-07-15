package com.future.demo.service;

import com.future.demo.mapper.CommonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
public class CommonService {
    @Resource
    CommonMapper commonMapper;

    /**
     * @param idempotentId
     * @param flag
     * @param count
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateIncreaseCount(String idempotentId, String flag, long count) {
        int affectRow = this.commonMapper.insertCountIdempotent(idempotentId);
        // 判断是否已经递增过，以免重复递增
        if (affectRow > 0)
            this.commonMapper.updateIncreaseCount(flag, count);
        else {
            if (log.isInfoEnabled())
                log.info("idempotentId {} flag {} count {} 已经递增过，导致放弃此次递增", idempotentId, flag, count);
        }
    }
}
