package com.future.demo;

import com.future.common.json.JSONUtil;
import com.future.demo.controller.Person;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DemoSpringBootMvcApplicationTests {
    @Autowired
    MockMvc mockMvc;

    @Test
    void contextLoads() throws Exception {
        // region 测试路径中使用通配符

        this.mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello!"));

        // 测试路径中使用?符号
        this.mockMvc.perform(get("/hella"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hell?!"));
        this.mockMvc.perform(get("/hell1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hell?!"));

        // 测试路径中使用*符号
        this.mockMvc.perform(get("/hell"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hell*!"));
        this.mockMvc.perform(get("/hellaaaaa"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hell*!"));
        this.mockMvc.perform(get("/hell11111"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hell*!"));

        // 测试路径中使用**符号
        this.mockMvc.perform(get("/hello/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello/**!"));
        this.mockMvc.perform(get("/hello/1/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello/**!"));
        this.mockMvc.perform(get("/hello/12/3/4/5"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello/**!"));

        // endregion

        // region 测试@RequestMapping注解的限制

        // 测试限制请求方法
        this.mockMvc.perform(post("/test1"))
                .andExpect(status().isOk())
                .andExpect(content().string("test1"));
        this.mockMvc.perform(get("/test1"))
                .andExpect(status().isMethodNotAllowed());

        // 测试限制请求参数
        this.mockMvc.perform(post("/test2")
                        .queryParam("username", "").queryParam("age", "18"))
                .andExpect(status().isOk())
                .andExpect(content().string("test2"));
        this.mockMvc.perform(post("/test2")
                        .queryParam("username", "").queryParam("age", "19"))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(post("/test2")
                        .queryParam("age", "18"))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(post("/test2")
                        .queryParam("username", "").queryParam("age", "18").queryParam("gender", "1"))
                .andExpect(status().isBadRequest());

        // 测试限制请求头
        this.mockMvc.perform(post("/test3")
                        .header("username", "").header("age", "18"))
                .andExpect(status().isOk())
                .andExpect(content().string("test3"));
        this.mockMvc.perform(post("/test3")
                        .header("username", "").header("age", "19"))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(post("/test3")
                        .header("age", "18"))
                .andExpect(status().isNotFound());

        // 测试请求体类型
        this.mockMvc.perform(post("/test4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("test4"));
        this.mockMvc.perform(post("/test4"))
                .andExpect(status().isUnsupportedMediaType());

        // 测试请求体类型
        this.mockMvc.perform(post("/test5"))
                .andExpect(status().isOk())
                .andExpect(content().string("<h1>test5</h1>"));

        // endregion

        // region 测试请求参数

        // 测试@RequestParam注解
        String name = "Dexter";
        Integer age = 18;
        this.mockMvc.perform(get("/api/v1/test1").queryParam("name", name).queryParam("age", age + ""))
                .andExpect(status().isOk())
                .andExpect(content().string("name=" + name + ",age=" + age));
        this.mockMvc.perform(get("/api/v1/test1").queryParam("name", name))
                .andExpect(status().isOk())
                .andExpect(content().string("name=" + name + ",age=15"));
        this.mockMvc.perform(get("/api/v1/test1"))
                .andExpect(status().isOk())
                .andExpect(content().string("name=null" + ",age=15"));

        // 测试pojo获取参数
        this.mockMvc.perform(get("/api/v1/test2").queryParam("name", name).queryParam("age", age + "")
                        .queryParam("hobby", "coding").queryParam("hobby", "读书")
                        .queryParam("address.country", "中国").queryParam("address.city", "广州市"))
                .andExpect(status().isOk())
                .andExpect(content().string("name=" + name + ",age=" + age + ",hobby=[coding, 读书],address=Person.Address(city=广州市, country=中国)"));

        // 测试@RequestHeader注解
        this.mockMvc.perform(get("/api/v1/test3").header("name", name).header("age", age + ""))
                .andExpect(status().isOk())
                .andExpect(content().string("name=" + name + ",age=" + age));

        // 测试@CookieValue注解
        this.mockMvc.perform(get("/api/v1/test4").cookie(new Cookie("name", name)).cookie(new Cookie("age", age + "")))
                .andExpect(status().isOk())
                .andExpect(content().string("name=" + name + ",age=" + age));

        // 测试@RequestBody注解
        Person person = new Person();
        person.setName("Dexter");
        person.setAge(18);
        person.setHobby(new String[]{"coding", "读书"});
        person.setAddress(new Person.Address("广州市", "中国"));
        this.mockMvc.perform(post("/api/v1/test5").contentType(MediaType.APPLICATION_JSON).content(JSONUtil.ObjectMapperInstance.writeValueAsBytes(person)))
                .andExpect(status().isOk())
                .andExpect(content().string("name=" + name + ",age=" + age + ",hobby=[coding, 读书],address=Person.Address(city=广州市, country=中国)"));

        // 测试文件上传
        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/test6")
                        .file(new MockMultipartFile("fileList", "hello".getBytes()))
                        .file(new MockMultipartFile("fileList", "hello1".getBytes()))
                        .queryParam("name", name).queryParam("age", age + "")
                        .queryParam("hobby", "coding").queryParam("hobby", "读书")
                        .queryParam("address.country", "中国").queryParam("address.city", "广州市"))
                .andExpect(status().isOk())
                .andExpect(content().string("name=" + name + ",age=" + age + ",hobby=[coding, 读书],address=Person.Address(city=广州市, country=中国),fileList=[hello, hello1]"));

        // endregion

        // region 测试响应体

        // 测试JSON响应体
        this.mockMvc.perform(get("/api/v1/response/test1"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"name\":\"张三\",\"age\":20,\"hobby\":[\"吃饭\",\"睡觉\",\"打豆豆\"],\"address\":{\"city\":\"北京市\",\"country\":\"海淀区\"}}"));

        // 测试下载文件
        this.mockMvc.perform(get("/api/v1/response/test2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello!"));

        // endregion
    }

}
