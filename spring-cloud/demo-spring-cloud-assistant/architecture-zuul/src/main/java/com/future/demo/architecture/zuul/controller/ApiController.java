package com.future.demo.architecture.zuul.controller;

import com.future.demo.architecture.common.feign.HelloworldClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/api/v1/zuul")
@Slf4j
public class ApiController {
    @Autowired
    HelloworldClient helloworldClient;

    @GetMapping(value = "test1", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> test1() throws UnknownHostException {
        String hostname = InetAddress.getLocalHost().getHostName();
        return helloworldClient.test1(hostname);
    }

}
