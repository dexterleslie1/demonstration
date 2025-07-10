package com.future.demo;

import com.future.common.json.JSONUtil;
import com.future.demo.controller.Person;
import com.future.demo.interceptor.MyHandlerInterceptor;
import com.future.demo.vo.request.PersonAddVo;
import com.future.demo.vo.request.PersonUpdateVo;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DemoSpringBootMvcApplicationTests {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MyHandlerInterceptor myHandlerInterceptor;

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

        // 测试请求路径参数
        this.mockMvc.perform(get("/api/v1/test7/" + name + "/" + age))
                .andExpect(status().isOk())
                .andExpect(content().string("name=Dexter,age=18"));
        this.mockMvc.perform(get("/api/v1/test7/ /" + age))
                .andExpect(status().isOk())
                .andExpect(content().string("name= ,age=18"));
        this.mockMvc.perform(get("/api/v1/test7/" + name + "/0"))
                .andExpect(status().isOk())
                .andExpect(content().string("name=Dexter,age=0"));

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
                .andExpect(content().string("{\"id\":null,\"name\":\"张三\",\"age\":20,\"hobby\":[\"吃饭\",\"睡觉\",\"打豆豆\"],\"address\":{\"city\":\"北京市\",\"country\":\"海淀区\"}}"));

        // 测试下载文件
        this.mockMvc.perform(get("/api/v1/response/test2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello!"));

        // endregion

        // region 测试restful api

        // 新增person
        PersonAddVo personAddVo = new PersonAddVo();
        personAddVo.setName("张三");
        personAddVo.setSex("男");
        personAddVo.setSex1("男");
        personAddVo.setAge(18);
        personAddVo.setHobby(new String[]{"吃饭", "睡觉", "打豆豆"});
        personAddVo.setAddress(new Person.Address("北京市", "海淀区"));
        personAddVo.setBookList(Arrays.asList("book1", "book2"));
        this.mockMvc.perform(post("/api/v1/restful/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.ObjectMapperInstance.writeValueAsBytes(personAddVo)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":null,\"data\":{\"id\":10,\"name\":\"张三\",\"age\":18,\"hobby\":[\"吃饭\",\"睡觉\",\"打豆豆\"],\"address\":{\"city\":\"北京市\",\"country\":\"海淀区\"}}}"));

        // 根据id获取person
        this.mockMvc.perform(get("/api/v1/restful/person/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":null,\"data\":{\"id\":2,\"name\":\"张三\",\"hobby\":[\"吃饭\",\"睡觉\",\"打豆豆\"],\"address\":{\"city\":\"北京市\",\"country\":\"海淀区\"}}}"));

        // 根据id更新person
        PersonUpdateVo personUpdateVo = new PersonUpdateVo();
        personUpdateVo.setId(10L);
        personUpdateVo.setName("张三");
        personUpdateVo.setAge(18);
        personUpdateVo.setHobby(new String[]{"吃饭", "睡觉", "打豆豆"});
        personUpdateVo.setAddress(new Person.Address("北京市", "海淀区"));
        this.mockMvc.perform(put("/api/v1/restful/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.ObjectMapperInstance.writeValueAsBytes(personUpdateVo)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":null,\"data\":{\"id\":10,\"name\":\"张三\",\"hobby\":[\"吃饭\",\"睡觉\",\"打豆豆\"],\"address\":{\"city\":\"北京市\",\"country\":\"海淀区\"}}}"));

        // 根据id删除person
        this.mockMvc.perform(delete("/api/v1/restful/person/11"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":null,\"data\":\"成功删除id=11\"}"));

        // endregion

        // region 测试拦截器

        // 拦截/hello请求
        this.mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello!"));
        Assertions.assertTrue(this.myHandlerInterceptor.isPreHandle());
        Assertions.assertTrue(this.myHandlerInterceptor.isPostHandle());
        Assertions.assertTrue(this.myHandlerInterceptor.isAfterCompletion());
        this.myHandlerInterceptor.reset();
        // 拦截/hello请求但preHandle返回false
        this.mockMvc.perform(get("/hello").param("preHandleReturnFalse", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        Assertions.assertTrue(this.myHandlerInterceptor.isPreHandle());
        Assertions.assertFalse(this.myHandlerInterceptor.isPostHandle());
        Assertions.assertFalse(this.myHandlerInterceptor.isAfterCompletion());
        this.myHandlerInterceptor.reset();
        // 不拦截/hella请求
        this.mockMvc.perform(get("/hella"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hell?!"));
        Assertions.assertFalse(this.myHandlerInterceptor.isPreHandle());
        Assertions.assertFalse(this.myHandlerInterceptor.isPostHandle());
        Assertions.assertFalse(this.myHandlerInterceptor.isAfterCompletion());
        this.myHandlerInterceptor.reset();

        // endregion

        // region 测试异常处理

        // 测试算数异常
        this.mockMvc.perform(get("/api/v1/exception/test1"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":\"算数异常\",\"data\":null}"));
        // 测试空指针异常
        this.mockMvc.perform(get("/api/v1/exception/test2"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":\"空指针异常\",\"data\":null}"));
        // 测试自定义异常
        this.mockMvc.perform(get("/api/v1/exception/test3"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":90000,\"errorMessage\":\"自定义异常\",\"data\":null}"));

        // endregion

        // region 测试spring数据校验

        // 英语语言
        personAddVo = new PersonAddVo();
        personAddVo.setName(" ");
        personAddVo.setSex("男");
        personAddVo.setSex1("男");
        personAddVo.setAge(-1);
        personAddVo.setHobby(new String[]{"吃饭", "睡觉", "打豆豆"});
        personAddVo.setAddress(new Person.Address("北京市", "海淀区"));
        personAddVo.setBookList(Arrays.asList("book1", "book2"));
        this.mockMvc.perform(post("/api/v1/restful/person")
                        .header("Accept-Language", "en-US")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.ObjectMapperInstance.writeValueAsBytes(personAddVo)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":\"参数校验失败\",\"data\":{\"name\":\"name is required\",\"age\":\"年龄不能小于0\"}}"));

        this.mockMvc.perform(get("/api/v1/testSingleParam")
                        .header("Accept-Language", "en-US")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"errorCode\":90000,\"errorMessage\":\"Parameter p1 not provide!\",\"data\":null}"));

        // 中文语言
        personAddVo = new PersonAddVo();
        personAddVo.setName(" ");
        personAddVo.setSex("男");
        personAddVo.setSex1("男");
        personAddVo.setAge(-1);
        personAddVo.setHobby(new String[]{"吃饭", "睡觉", "打豆豆"});
        personAddVo.setAddress(new Person.Address("北京市", "海淀区"));
        personAddVo.setBookList(Arrays.asList("book1", "book2"));
        this.mockMvc.perform(post("/api/v1/restful/person")
                        .header("Accept-Language", "zh-CN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.ObjectMapperInstance.writeValueAsBytes(personAddVo)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":\"参数校验失败\",\"data\":{\"name\":\"名称必须\",\"age\":\"年龄不能小于0\"}}"));

        this.mockMvc.perform(get("/api/v1/testSingleParam")
                        .header("Accept-Language", "zh-CN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"errorCode\":90000,\"errorMessage\":\"没有提供p1参数！\",\"data\":null}"));

        // 测试Pattern校验
        personAddVo = new PersonAddVo();
        personAddVo.setName("张三");
        personAddVo.setSex("男1");
        personAddVo.setSex1("男");
        personAddVo.setAge(12);
        personAddVo.setHobby(new String[]{"吃饭", "睡觉", "打豆豆"});
        personAddVo.setAddress(new Person.Address("北京市", "海淀区"));
        personAddVo.setBookList(Arrays.asList("book1", "book2"));
        this.mockMvc.perform(post("/api/v1/restful/person")
                        .header("Accept-Language", "zh-CN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.ObjectMapperInstance.writeValueAsBytes(personAddVo)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":\"参数校验失败\",\"data\":{\"sex\":\"性别只能是男或者女\"}}"));

        // 测试自定义校验注解
        personAddVo = new PersonAddVo();
        personAddVo.setName("张三");
        personAddVo.setSex("男");
        personAddVo.setSex1("男1");
        personAddVo.setAge(12);
        personAddVo.setHobby(new String[]{"吃饭", "睡觉", "打豆豆"});
        personAddVo.setAddress(new Person.Address("北京市", "海淀区"));
        personAddVo.setBookList(Arrays.asList("book1", "book2"));
        this.mockMvc.perform(post("/api/v1/restful/person")
                        .header("Accept-Language", "zh-CN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.ObjectMapperInstance.writeValueAsBytes(personAddVo)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":\"参数校验失败\",\"data\":{\"sex1\":\"性别只能是男或者女\"}}"));
        personAddVo = new PersonAddVo();
        personAddVo.setName("张三");
        personAddVo.setSex("男");
        personAddVo.setSex1("男1");
        personAddVo.setAge(12);
        personAddVo.setHobby(new String[]{"吃饭", "睡觉", "打豆豆"});
        personAddVo.setAddress(new Person.Address("北京市", "海淀区"));
        personAddVo.setBookList(Arrays.asList("book1", "book2"));
        this.mockMvc.perform(post("/api/v1/restful/person")
                        .header("Accept-Language", "en-US")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.ObjectMapperInstance.writeValueAsBytes(personAddVo)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":\"参数校验失败\",\"data\":{\"sex1\":\"Gender is male or female\"}}"));

        // 测试校验 String[] 数组长度
        personAddVo = new PersonAddVo();
        personAddVo.setName("张三");
        personAddVo.setSex("男");
        personAddVo.setSex1("男");
        personAddVo.setAge(12);
        personAddVo.setAddress(new Person.Address("北京市", "海淀区"));
        personAddVo.setBookList(Arrays.asList("book1", "book2"));
        this.mockMvc.perform(post("/api/v1/restful/person")
                        .header("Accept-Language", "en-US")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.ObjectMapperInstance.writeValueAsBytes(personAddVo)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":\"参数校验失败\",\"data\":{\"hobby\":\"请指定爱好\"}}"));

        // 测试校验 List<String> 数据
        personAddVo = new PersonAddVo();
        personAddVo.setName("张三");
        personAddVo.setSex("男");
        personAddVo.setSex1("男");
        personAddVo.setAge(12);
        personAddVo.setHobby(new String[]{"h1"});
        personAddVo.setAddress(new Person.Address("北京市", "海淀区"));
        this.mockMvc.perform(post("/api/v1/restful/person")
                        .header("Accept-Language", "en-US")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSONUtil.ObjectMapperInstance.writeValueAsBytes(personAddVo)))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":\"参数校验失败\",\"data\":{\"bookList\":\"请指定书本列表\"}}"));


        // endregion
    }

}
