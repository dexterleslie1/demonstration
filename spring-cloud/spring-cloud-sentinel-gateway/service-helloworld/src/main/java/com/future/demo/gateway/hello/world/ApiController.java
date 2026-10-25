package com.future.demo.gateway.hello.world;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ApiController {

    /** group-a 精确匹配 */
    @GetMapping("/api/v1/a/exact")
    public ResponseEntity<String> groupAExact() throws InterruptedException {
        Thread.sleep(100);
        return ResponseEntity.ok("group-a exact ok");
    }

    /** group-a 前缀匹配 */
    @GetMapping("/api/v1/a/pattern/demo")
    public ResponseEntity<String> groupAPattern() throws InterruptedException {
        Thread.sleep(100);
        return ResponseEntity.ok("group-a pattern ok");
    }

    @GetMapping("/api/v1/b/demo")
    public ResponseEntity<String> groupB() throws InterruptedException {
        Thread.sleep(100);
        return ResponseEntity.ok("group-b ok");
    }
}
