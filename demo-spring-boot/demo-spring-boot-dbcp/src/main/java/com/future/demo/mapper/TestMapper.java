package com.future.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TestMapper {

    @Select("select sleep(0.5)")
    Integer selectSleep();
}
