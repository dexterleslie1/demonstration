package com.future.demo.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestPropertiesPrinter {
    @Autowired
    TestProperties testProperties;

    public String print() {
//        Assert.assertEquals("string1", testProperties.getProp1());
//        Assert.assertEquals(8080, testProperties.getProp2());
//        Assert.assertArrayEquals(Arrays.asList("v1", "v2").toArray(), testProperties.getProp3().toArray());
//        Map<String, String> mapper = new HashMap<>();
//        mapper.put("k1", "vv1");
//        mapper.put("k2", "vv2");
//        Assert.assertArrayEquals(mapper.keySet().toArray(), testProperties.getProp4().keySet().toArray());
//        Assert.assertArrayEquals(mapper.entrySet().toArray(), testProperties.getProp4().entrySet().toArray());
//        Assert.assertEquals("nv1", testProperties.getNested().getProp1());
//        Assert.assertEquals(8090, testProperties.getNested().getProp2());

        ConfigurationProperties configurationProperties =
                TestProperties.class.getAnnotation(ConfigurationProperties.class);
        String prefix = configurationProperties.prefix();

        StringBuilder builder = new StringBuilder();
        builder.append(prefix + ".prop1=" + testProperties.getProp1());
        return builder.toString();
    }
}
