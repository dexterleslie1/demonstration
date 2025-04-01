package com.future.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Dexterleslie.Chan
 */
@RestController
@RequestMapping(value = "/api")
public class ApiController {
    private List<byte[]> bytesHolder = new ArrayList<>();

    /**
     * @return
     */
    @GetMapping(value = "consume5mbmemory")
    public ResponseEntity<Map<String, Object>> consume5mbmemory() {
        bytesHolder.add(new byte[5 * 1024 * 1024]);
        return null;
    }
}
