package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Dexterleslie.Chan
 */
@RestController
@RequestMapping(value="/api")
@Slf4j
public class ApiController {

    @Resource
    MyService myService;

    /**
     *
     * @return
     */
    @GetMapping(value="test1")
    public ResponseEntity<String> test1(){
        log.info("Api for testing is called.");
        return ResponseEntity.ok("Hello ....");
    }

    @GetMapping(value="test2")
    public ResponseEntity<String> test2(@RequestParam(value = "param1", defaultValue = "") String param1){
        String str = this.myService.test1(param1);
        return ResponseEntity.ok(str);
    }

    @GetMapping(value="test21")
    public ResponseEntity<String> test21(@RequestParam(value = "param2", defaultValue = "") String param2) throws Exception {
        String str = this.myService.test2(param2);
        return ResponseEntity.ok(str);
    }

}
