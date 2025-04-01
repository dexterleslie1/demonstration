package com.future.demo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
public class StaticLoggerFieldInjectionTests {
    @Autowired
    MockMvc mockMvc;

    @LocalServerPort
    int port;

    // 用于注入mock的logger
    @Mock
    Logger log;

    // 用于替换通过lombok @Slf4j注入的静态log字段
    @Autowired
    ApiController apiController;

    @Autowired
    private RestTemplate restTemplate = null;

    @Before
    public void setup() throws Exception {
        // 初始化@Mock注解的字段
        MockitoAnnotations.initMocks(this);
        // 替换ApiController对象中的静态log为mock logger
        setFinalStatic(ApiController.class.getDeclaredField("log"), log);
    }

    // 替换静态字段
    public static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    @Test
    public void test1() throws Exception {
        this.mockMvc.perform(get("/api/test1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello ...."));

        // 用于验证是否使用指定的参数调用log.info(...)方法
        Mockito.verify(log).info("Api for testing is called.");
    }

}
