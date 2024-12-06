package com.future.demo;

import com.future.demo.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@AutoConfigureMockMvc
public class ApplicationTests {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    TestService testService;

    @Test
    public void contextLoads() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":null,\"data\":{\"prop1\":\"astring1\",\"prop2\":18080,\"prop3\":[\"av1\",\"av2\"],\"prop4\":{\"k1\":\"avv1\",\"k2\":\"avv2\"},\"nested\":{\"prop1\":\"anv1x\",\"prop2\":18090}}}"));

        Assertions.assertEquals("astring1", this.testService.getTestProperties().getProp1());
        Assertions.assertEquals(2, this.testService.getTestProperties().getProp3().size());
        Assertions.assertEquals("avv1", this.testService.getTestProperties().getProp4().get("k1"));
        Assertions.assertEquals("anv1x", this.testService.getTestProperties().getNested().getProp1());
        Assertions.assertEquals(18090, this.testService.getTestProperties().getNested().getProp2());
    }
}
