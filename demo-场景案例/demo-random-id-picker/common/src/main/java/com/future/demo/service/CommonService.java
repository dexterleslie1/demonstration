package com.future.demo.service;

import cn.hutool.core.util.RandomUtil;
import com.future.demo.mapper.CommonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class CommonService {
    @Resource
    CommonMapper commonMapper;

    /**
     * 初始化id列表相关表
     *
     * @param flag
     */
    public void init(String flag) {
        commonMapper.createTable(flag);
        commonMapper.insertFlagCreated(flag);
    }

    /**
     * 重置指定flag的id，重置后id数据被清空，需要重新新增id列表
     *
     * @param flag
     */
    public void reset(String flag) {
        commonMapper.resetDropTable(flag);
        commonMapper.deleteFlagCreated(flag);
    }

    /**
     * 获取随机id列表
     *
     * @param flag
     * @param size
     * @return
     */
    public List<Long> listIdRandomly(String flag, int size) {
        if (size < 0) {
            size = 0;
        }
        Long maxId = commonMapper.getMaxId(flag);
        Long start = RandomUtil.randomLong(1, maxId + 1);
        return commonMapper.listBizIdList(flag, start, size);
    }
}
