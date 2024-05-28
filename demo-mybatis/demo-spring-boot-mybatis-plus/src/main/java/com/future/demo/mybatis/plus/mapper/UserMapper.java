package com.future.demo.mybatis.plus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.demo.mybatis.plus.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User> {
    @Update("<script>" +
            "update `user`\n" +
            "<trim prefix=\"set\" suffixOverrides=\",\">\n" +
            "   <trim prefix=\"age =case\" suffix=\"end\">\n" +
            "       <foreach collection=\"ageList\" item=\"i\" index=\"index\">\n" +
            "           <if test=\"i.age!=null\">\n" +
            "               when id=${i.id} then ${i.age}\n" +
            "           </if>\n" +
            "       </foreach>\n" +
            "   </trim>\n" +
            "</trim>\n" +
            "where\n" +
            "<foreach collection=\"ageList\" separator=\"or\" item=\"i\" index=\"index\" >\n" +
            " ${i.id}\n" +
            "</foreach>\n" +
            "</script>")
    void updateAge(@Param("ageList") List<Map<String, Object>> ageList);
}
