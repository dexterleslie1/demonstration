package com.future.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/v1")
public class ApiController {
    @Autowired
    TestComponent testComponent;

    @GetMapping(value = "")
    ResponseEntity<String> test() throws Exception {
        this.testComponent.test();
        return ResponseEntity.ok("ok!");
    }

}
