package com.future.demo.openresty.api;

import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @author Dexterleslie.Chan
 */
@RestController
@RequestMapping(value="/api/v1")
public class ApiController {
    /**
     * 用于调试openresty an upstream response is buffered to a temporary file警告
     * @return
     */
    @GetMapping(value="responseWithLargeBody")
    public ResponseEntity<String> responseWithLargeBody() {
        byte []bytes = new byte[1024*512];
        Random random = new Random();
        random.nextBytes(bytes);
        String largeResponseBody = Base64Utils.encodeToString(bytes);
        return ResponseEntity.ok().body(largeResponseBody);
    }
}
