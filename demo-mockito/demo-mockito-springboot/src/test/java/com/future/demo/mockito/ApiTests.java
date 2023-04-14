package com.future.demo.mockito;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes={Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ApiTests {
    @LocalServerPort
    int port;

    @Mock
    Logger log;

    @Autowired
    ApiController apiController;

    @Autowired
    private RestTemplate restTemplate = null;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        setFinalStatic(ApiController.class.getDeclaredField("log"), log);
    }

    public static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    @Test
    public void test1() {
        Mockito.doNothing().when(log).info("Api for testing is called.");

        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "http://localhost:"+ port + "/api/test1",
                String.class);
        Assert.assertEquals("Hello ....", response.getBody());

        Mockito.verify(log).info("Api for testing is called.");
    }

}
