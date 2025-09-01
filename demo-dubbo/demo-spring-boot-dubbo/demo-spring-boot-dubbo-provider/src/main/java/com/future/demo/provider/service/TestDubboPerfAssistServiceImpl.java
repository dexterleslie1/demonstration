package com.future.demo.provider.service;

import cn.hutool.core.util.RandomUtil;
import com.future.demo.common.service.TestDubboPerfAssistService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 需要使用 Dubbo 的 Service 注解暴露服务
@org.apache.dubbo.config.annotation.Service
public class TestDubboPerfAssistServiceImpl implements TestDubboPerfAssistService {
    private int totalKey = 100000;
    private List<String> keyList = new ArrayList<>();

    @PostConstruct
    public void init() {
        // 生成10w个随机key放到内中
        for (int i = 0; i < totalKey; i++) {
            String key = UUID.randomUUID().toString();
            keyList.add(key);
        }
    }

    @Override
    public String getRandomUuid() {
        int randomInt = RandomUtil.randomInt(0, totalKey);
        String uuid = this.keyList.get(randomInt);
        return "UUID:" + uuid;
    }
}
