package com.future.demo.mybatis.plus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.demo.mybatis.plus.entity.AliveTestingEntity;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

public interface AliveTestingMapper extends BaseMapper<AliveTestingEntity> {
    @Select("select now() from dual")
    Date checkAlive();
}
