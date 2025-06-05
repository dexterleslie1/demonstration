package com.future.demo;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.future.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CommonMapper {

    @Resource
    Session session;

    public void truncate(String table) throws BusinessException {
        String cql = "truncate table " + table;
        ResultSet resultSet = this.session.execute(cql);
        if (!resultSet.wasApplied()) {
            throw new BusinessException("执行 " + cql + "时错误");
        }
    }
}
