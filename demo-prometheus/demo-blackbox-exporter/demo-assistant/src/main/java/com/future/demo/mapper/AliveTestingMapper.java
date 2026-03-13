package com.future.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.demo.entity.AliveTestingEntity;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

public interface AliveTestingMapper extends BaseMapper<AliveTestingEntity> {
    @Select("select now() from dual")
    Date checkAlive();
}
