package com.future.demo.mapper;

import com.future.demo.bean.Dict;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DictMapper {
    @Insert("insert into `dict`(id,`name`,`value`) values(#{id},#{name},#{value})")
    void add(Dict dict);

    @Select("select id,`name`,`value` from `dict` where id=#{id}")
    Dict getById(Integer id);

    @Select("select id,`name`,`value` from `dict`")
    List<Dict> listAll();

    @Delete("delete from `dict` where id=#{id}")
    void delete(Integer id);

    @Update("truncate table `dict`")
    void truncate();
}
