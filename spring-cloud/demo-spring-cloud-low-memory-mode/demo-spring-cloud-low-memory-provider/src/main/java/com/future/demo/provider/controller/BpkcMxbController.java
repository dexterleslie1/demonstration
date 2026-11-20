package com.future.demo.provider.controller;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.demo.common.entity.BpkcMxb;
import com.future.demo.provider.service.BpkcMxbService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UncheckedIOException;

@RestController
@RequestMapping("/api/v1/bpkc-mxb")
public class BpkcMxbController {
    @Resource
    private BpkcMxbService bpkcMxbService;
    @Resource
    private ObjectMapper objectMapper;

    @GetMapping("random")
    public void random(@RequestParam(value = "limit", defaultValue = "20000") Integer limit,
                       HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        try (JsonGenerator gen = this.objectMapper.getFactory().createGenerator(response.getOutputStream())) {
            gen.writeStartObject();
            gen.writeNumberField("errorCode", 0);
            gen.writeFieldName("data");
            gen.writeStartArray();
            this.bpkcMxbService.streamRandomList(limit, batch -> {
                try {
                    for (BpkcMxb row : batch) {
                        gen.writeObject(row);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
            gen.writeEndArray();
            gen.writeEndObject();
        }
    }
}
