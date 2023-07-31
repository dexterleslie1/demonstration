package com.future.demo.architecture.hello.world;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    @GetMapping(value = "/api/v1/test1", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> test1(@RequestHeader(value = "x-internal-servername", defaultValue = "", required = false) String xInternalServername) {
        return ResponseEntity.ok("xInternalServername=" + xInternalServername);
    }

    @GetMapping(value = "/api/v1/test2", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> test2(@RequestHeader(value = "x-internal-servername", defaultValue = "", required = false) String xInternalServername) {
        return ResponseEntity.ok("xInternalServername=" + xInternalServername);
    }
}
