package com.future.demo.mapper;

import com.future.demo.entity.PerformanceTest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface PerformanceTestMapper {
//    // 简单查询
//    @Select("SELECT * FROM performance_test WHERE id = #{id}")
//    PerformanceTest selectById(Long id);

    // 范围查询
    @Select("SELECT * FROM performance_test WHERE value>=#{min} ORDER BY value LIMIT #{limit}")
    List<PerformanceTest> selectByValueRange(@Param("min") int min, @Param("limit") int limit);

    @Update("update performance_test set value=#{value} where id=#{id}")
    int updateValue(@Param("id") int id, @Param("value") int value);

//    // 全表扫描
//    @Select("SELECT * FROM performance_test WHERE description LIKE CONCAT('%', #{keyword}, '%') LIMIT 1000")
//    List<PerformanceTest> fullScanSearch(@Param("keyword") String keyword);
//
//    // 计数查询
//    @Select("SELECT COUNT(*) FROM performance_test WHERE value > #{threshold}")
//    int countAboveThreshold(int threshold);

    @Select("select min(id) from performance_test")
    int selectIdMin();

    @Select("select max(id) from performance_test")
    int selectIdMax();
}
