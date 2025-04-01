package com.future.demo.vo.request;

import com.future.demo.controller.Person;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PersonUpdateVo {
    private Long id;
    // 使用@NotBlank注解，表示name字段不能为null，且不能为空白字符串
    // {person.name.required}是为了国际化，具体的错误信息可以通过配置文件来配置
    @NotBlank(message = "{person.name.required}")
    private String name;

    @Max(value = 150, message = "年龄不能大于150")
    @Min(value = 0, message = "年龄不能小于0")
    private int age;
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
