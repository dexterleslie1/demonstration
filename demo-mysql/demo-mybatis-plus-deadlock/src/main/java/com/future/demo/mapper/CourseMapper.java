package com.future.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.demo.entity.Course;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CourseMapper extends BaseMapper<Course> {
    @Select("select * from course where id=#{id} lock in share mode")
    Course selectLockInShareMode(@Param("id") Long id);
}
