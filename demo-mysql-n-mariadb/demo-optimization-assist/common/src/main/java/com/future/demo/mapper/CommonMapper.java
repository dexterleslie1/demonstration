package com.future.demo.mapper;

import com.future.demo.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Mapper
public interface CommonMapper {
    /**
     * 批量插入 Employee
     *
     * @param employeeList
     */
    @Insert("<script>" +
            "insert into employees(name,age,position,remark)" +
            "values " +
            "<foreach collection='employeeList' item='e' separator=','>" +
            "(#{e.name},#{e.age},#{e.position},#{e.remark})" +
            "</foreach>" +
            "</script>")
    void insertEmployeeBatch(List<Employee> employeeList);

    /**
     * 查询所有 Employee 信息
     *
     * @return
     */
    @Select("select * from employees")
    List<Employee> selectAllEmployee();

    /**
     * 查询所有 Employee 的 name
     *
     * @return
     */
    @Select("select distinct name from employees")
    List<String> selectAllEmployeeName();

    /**
     * 最左前缀法则 - 全值匹配
     *
     * @param name
     * @param age
     * @param position
     * @return
     */
    @Select("select * from employees where name=#{name} and age=#{age} and position=#{position}")
    List<Employee> selectLeftMostFully(@RequestParam("name") String name,
                                       @RequestParam("age") Integer age,
                                       @RequestParam("position") String position);

    /**
     * 最左前缀法则 - 没有中间列
     *
     * @return
     */
    @Select("select * from employees where name=#{name} and position=#{position}")
    List<Employee> selectLeftMostWithoutMiddleColumn(@RequestParam("name") String name,
                                                     @RequestParam("position") String position);

    /**
     * 最左前缀法则 - 没有开始列
     *
     * @return
     */
    @Select("select * from employees where age=#{age} and position=#{position}")
    List<Employee> selectLeftMostWithoutStartColumn(@RequestParam("age") Integer age,
                                                    @RequestParam("position") String position);

    /**
     * 测试覆盖索引性能 - 使用 * 时
     *
     * @param name
     * @param age
     * @param position
     * @return
     */
    @Select("select * from employees where name=#{name} and age=#{age} and position=#{position}")
    List<Employee> testCoveringIndexPerfWithAsterisk(@RequestParam("name") String name,
                                                     @RequestParam("age") Integer age,
                                                     @RequestParam("position") String position);

    /**
     * 测试覆盖索引性能 - 不使用 * 时
     *
     * @param name
     * @param age
     * @param position
     * @return
     */
    @Select("select id,name,age,position from employees where name=#{name} and age=#{age} and position=#{position}")
    List<Employee> testCoveringIndexPerfWithoutAsterisk(@RequestParam("name") String name,
                                                        @RequestParam("age") Integer age,
                                                        @RequestParam("position") String position);
}
