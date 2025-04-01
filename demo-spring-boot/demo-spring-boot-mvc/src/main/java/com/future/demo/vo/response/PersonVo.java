package com.future.demo.vo.response;

import com.future.demo.controller.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 返回person数据vo
@Data
public class PersonVo {
    Long id;
    private String name;

    // 注意：不添加age字段是为了模拟脱敏需求，不返回年龄数据给前端
    // private int age;

    private String [] hobby;
    private Person.Address address;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address {
        private String city;
        private String country;
    }
}
