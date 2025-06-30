package com.future.demo.mapper;

import com.datastax.driver.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 提醒：下面的逻辑不放在CassandraMapper中是为了避免CassandraMapper和PrometheusCustomMonitor循环依赖
 */
@Component
public class CassandraCountMapper {
    @Resource
    Session session;
    private PreparedStatement preparedStatementGetCountByFlag;

    @PostConstruct
    public void init() {
        String cql = "select count from t_count where flag=?";
        preparedStatementGetCountByFlag = session.prepare(cql);
        preparedStatementGetCountByFlag.setConsistencyLevel(ConsistencyLevel.LOCAL_ONE);
    }

    /**
     * 根据flag获取count
     *
     * @param flag
     * @return
     */
    public Long getCountByFlag(String flag) {
        BoundStatement boundStatement = preparedStatementGetCountByFlag.bind(flag);
        ResultSet resultSet = session.execute(boundStatement);
        return resultSet.one().getLong("count");
    }
}
