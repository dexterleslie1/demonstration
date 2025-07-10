package com.future.demo.vo.request;

import com.future.demo.annotations.Gender;
import com.future.demo.controller.Person;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 新增person vo
@Data
public class PersonAddVo {

    // 使用@NotBlank注解，表示name字段不能为null，且不能为空白字符串
    // {person.name.required}是为了国际化，具体的错误信息可以通过配置文件来配置
    @NotBlank(message = "{person.name.required}")
    private String name;

    @Max(value = 150, message = "年龄不能大于150")
    @Min(value = 0, message = "年龄不能小于0")
    private int age;

    @Pattern(regexp = "^[男|女]$", message = "性别只能是男或者女")
    @NotBlank(message = "性别只能是男或者女")
    private String sex;

    @Gender
    private String sex1;

    @NotNull(message = "请指定爱好")
    @Size(min = 1, message = "请指定爱好")
    private String[] hobby;
    private Person.Address address;

    // 测试List数据校验
    @NotEmpty(message = "请指定书本列表")
    private List<String> bookList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address {
        private String city;
        private String country;
    }
}
