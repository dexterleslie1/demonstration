package com.future.demo.argument;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/handler/argument")
public class RequestParamDemoController {

    @RequestMapping(path = "testRequestParam1")
    public ResponseEntity<String> testRequestParam1(
            @RequestParam(value = "contentList", required = false, defaultValue = "") List<String> contentList) {
        String str = "contentList=" + contentList + ",size=" + contentList.size();
        return ResponseEntity.ok(str);
    }
}
