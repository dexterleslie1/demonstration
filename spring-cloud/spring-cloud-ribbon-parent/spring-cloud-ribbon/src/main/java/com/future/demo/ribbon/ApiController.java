package com.future.demo.ribbon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@RestController
public class ApiController {
    @Autowired
    RestTemplate restTemplate;

    /**
     * @param name
     * @return
     */
    @RequestMapping(value = "/api/v1/external/sayHello", method = RequestMethod.GET)
    public String sayHello(@RequestParam(value = "name", defaultValue = "") String name,
                           @RequestParam(value = "timeoutInMillis", defaultValue = "0") int timeoutInMillis) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("timeoutInMillis", String.valueOf(timeoutInMillis));
        return restTemplate.postForObject("http://spring-cloud-helloworld/api/v1/sayHello?name={name}&timeoutInMillis={timeoutInMillis}", null, String.class, params);
    }
}
