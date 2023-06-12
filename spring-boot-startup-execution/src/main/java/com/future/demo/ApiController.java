package com.future.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping(value = "/api/v1")
public class ApiController {
    private final static Logger logger = LoggerFactory.getLogger(ApiController.class);

    /**
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value = "test1")
    public ResponseEntity<String> test1(
            HttpServletRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok("Server is ready.");
    }
}
