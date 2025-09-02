package com.future.demo.service;

import cn.hutool.core.util.RandomUtil;
import com.future.demo.entity.PerformanceTest;
import com.future.demo.mapper.PerformanceTestMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Service
public class PerformanceTestService {
    @Resource
    PerformanceTestMapper performanceTestMapper;

    private int minId;
    private int maxId;

    @PostConstruct
    public void init() {
        minId = performanceTestMapper.selectIdMin();
        maxId = performanceTestMapper.selectIdMax();
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    public List<PerformanceTest> selectByValueRangeWithIsolationReadUncommitted() {
        int limit = 20;
        int randomInt = RandomUtil.randomInt(0, 1000 - limit);
        return performanceTestMapper.selectByValueRange(randomInt, limit);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public List<PerformanceTest> selectByValueRangeWithIsolationReadCommitted() {
        int limit = 20;
        int randomInt = RandomUtil.randomInt(0, 1000 - limit);
        return performanceTestMapper.selectByValueRange(randomInt, limit);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public List<PerformanceTest> selectByValueRangeWithIsolationRepeatableRead() {
        int limit = 20;
        int randomInt = RandomUtil.randomInt(0, 1000 - limit);
        return performanceTestMapper.selectByValueRange(randomInt, limit);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public List<PerformanceTest> selectByValueRangeWithIsolationSerializable() {
        int limit = 20;
        int randomInt = RandomUtil.randomInt(0, 1000 - limit);
        return performanceTestMapper.selectByValueRange(randomInt, limit);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateValue() {
        int randomId = RandomUtil.randomInt(minId, maxId + 1);
        int randomValue = (int) (RandomUtil.randomFloat() * 1000);
        performanceTestMapper.updateValue(randomId, randomValue);
    }
}
