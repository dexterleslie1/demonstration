package com.future.demo.mybatis.plus.service;

import com.future.demo.mybatis.plus.mapper.DBPartitionMapper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DefaultDBPartitionService {
    @Autowired
    DBPartitionMapper dbPartitionMapper;

    public void add() {
        Date timeNow = new Date();
        String partitionName = "p" + DateFormatUtils.format(timeNow, "yyyyMMdd");
        Date dateTemporary = DateUtils.truncate(timeNow, Calendar.DATE);
        dateTemporary = DateUtils.addDays(dateTemporary, 1);
        this.dbPartitionMapper.add(partitionName, dateTemporary);
    }

    public void delete() {
        Date timeNow = new Date();
        String partitionName = "p" + DateFormatUtils.format(timeNow, "yyyyMMdd");
        this.dbPartitionMapper.delete(partitionName);
    }

    public List<Map<String, Object>> findPartitions() {
        List<Map<String, Object>> partitions = this.dbPartitionMapper.findPartitions();
        return partitions;
    }
}
