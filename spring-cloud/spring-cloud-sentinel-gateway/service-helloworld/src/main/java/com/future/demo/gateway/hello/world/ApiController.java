package com.future.demo.gateway.hello.world;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ApiController {

    /** group-a 精确匹配；p1=v2 时网关并发阈值为 3，否则为 2 */
    @GetMapping("/api/v1/a/exact")
    public ResponseEntity<String> groupAExact(
            @RequestParam(value = "p1", defaultValue = "") String p1) throws InterruptedException {
        Thread.sleep(100);
        return ResponseEntity.ok("group-a exact ok, p1=" + p1);
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
