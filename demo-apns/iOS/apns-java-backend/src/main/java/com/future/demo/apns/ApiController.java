package com.future.demo.apns;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/api/v1")
@Slf4j
public class ApiController {
    @Value("${certificateBase64}")
    private String certificateBase64;
    @Value("${passphrase}")
    private String passphrase;
    @Value("${production}")
    private boolean production;
    @Value("${apnsId}")
    private String apnsId;

    @Resource
    ApnsTesterService apnsTesterService;

    @GetMapping(value = "test1")
    public ResponseEntity<String> test1() throws InterruptedException, ExecutionException, IOException {
        String uuid = UUID.randomUUID().toString();
        String title = "测试标题" + uuid;
        String content = "测试内容" + uuid;
        return ResponseEntity.ok(this.apnsTesterService.testApple(certificateBase64, passphrase, production, title, content, apnsId));
    }
}
