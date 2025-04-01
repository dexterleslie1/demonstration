package com.future.demo.mybatis.plus.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.common.exception.BusinessException;
import com.future.common.http.PageResponse;
import com.future.demo.mybatis.plus.Application;
import com.future.demo.mybatis.plus.entity.OperationType;
import com.future.demo.mybatis.plus.mapper.OperationLogMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class OperationLogServiceTests {

    @Resource
    OperationLogService operationLogService;
    @Resource
    OperationLogMapper operationLogMapper;

    @Before
    public void setup() {
        this.operationLogMapper.delete(Wrappers.query());
    }

    @Test
    public void test() throws BusinessException {
        Long userId = 10000L;
        OperationType operationType = OperationType.AddType;
        String content1 = UUID.randomUUID().toString();
        this.operationLogService.add(userId, userId, userId, operationType, content1);

        Long userId2 = 10001L;
        OperationType operationType2 = OperationType.DeleteType;
        String content2 = UUID.randomUUID().toString();
        this.operationLogService.add(userId2, userId2, userId2, operationType2, content2);

        PageResponse<OperationLogService.OperationLogVo> pageResponse =
                this.operationLogService.list(userId, null, 1, 10);
        Assert.assertEquals(content1, pageResponse.getData().get(0).getContent());
        Assert.assertEquals(OperationType.AddType.getDescription(), pageResponse.getData().get(0).getOperationType());

        pageResponse =
                this.operationLogService.list(userId2, null, 1, 10);
        Assert.assertEquals(content2, pageResponse.getData().get(0).getContent());
        Assert.assertEquals(OperationType.DeleteType.getDescription(), pageResponse.getData().get(0).getOperationType());
    }

}
