package com.future.demo.mockito;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dexterleslie.Chan
 */
@RestController
@RequestMapping(value="/api")
@Slf4j
public class ApiController {
    /**
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value="test1")
    public ResponseEntity<String> test1(
            HttpServletRequest request,
            HttpServletResponse response){
        log.info("Api for testing is called.");
        return ResponseEntity.ok("Hello ....");
    }
}
