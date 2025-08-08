package com.future.demo;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.future.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CommonMapper {

    @Resource
    // cassandra3 驱动程序
    // Session session;
    // cassandra5 驱动程序
    CqlSession session;

    public void truncate(String table) throws BusinessException {
        String cql = "truncate table " + table;
        ResultSet resultSet = this.session.execute(cql);
        if (!resultSet.wasApplied()) {
            throw new BusinessException("执行 " + cql + "时错误");
        }
    }
}
