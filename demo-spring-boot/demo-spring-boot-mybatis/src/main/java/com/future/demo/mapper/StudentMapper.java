package com.future.demo.mapper;

import com.future.demo.bean.Student;

/**
* @author dexterleslie
* @description 针对表【student】的数据库操作Mapper
* @createDate 2024-12-04 17:26:46
* @Entity com.future.demo.bean.Student
*/
public interface StudentMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Student record);

    int insertSelective(Student record);

    Student selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Student record);

    int updateByPrimaryKey(Student record);

}
