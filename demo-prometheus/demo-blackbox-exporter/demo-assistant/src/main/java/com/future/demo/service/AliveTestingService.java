package com.future.demo.service;

import com.future.demo.mapper.AliveTestingMapper;
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
