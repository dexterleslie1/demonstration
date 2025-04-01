package com.future.demo;

import com.future.demo.mapper.OrderMapper;
import com.future.demo.util.BatchInsertionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class OrderTests {

    @Autowired
    SqlSessionFactory sqlSessionFactory;
    @Autowired
    OrderMapper orderMapper;

    @Test
    public void test() {
        orderMapper.truncate();
        BatchInsertionUtil.batchInsert(10000 * 100, orderMapper, sqlSessionFactory, log);
    }
}
