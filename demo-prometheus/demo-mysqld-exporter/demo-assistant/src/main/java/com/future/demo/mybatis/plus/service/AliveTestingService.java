package com.future.demo.mybatis.plus.service;

import com.future.demo.mybatis.plus.mapper.AliveTestingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AliveTestingService {
    @Autowired
    AliveTestingMapper aliveTestingMapper;

    public void checkAlive() {
        this.aliveTestingMapper.checkAlive();
    }
}
