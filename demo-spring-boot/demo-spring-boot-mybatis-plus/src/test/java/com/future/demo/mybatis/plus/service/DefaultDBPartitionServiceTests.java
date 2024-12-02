package com.future.demo.mybatis.plus.service;

import com.future.demo.mybatis.plus.Application;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class DefaultDBPartitionServiceTests {
    @Autowired
    DefaultDBPartitionService dbPartitionService;

    @Test
    public void add() {
        this.dbPartitionService.delete();
        this.dbPartitionService.add();
    }

    @Test
    public void delete() {
        try {
            this.dbPartitionService.add();
        } catch (Exception ex) {
            // 忽略已经存在错误
        }
        this.dbPartitionService.delete();
    }

    @Test
    public void findPartitions() {
        try {
            this.dbPartitionService.add();
        } catch (Exception ex) {
            // 忽略分区已存在错误
        }

        List<Map<String, Object>> partitions = this.dbPartitionService.findPartitions();
        Assert.assertEquals(1, partitions.size());
    }
}
