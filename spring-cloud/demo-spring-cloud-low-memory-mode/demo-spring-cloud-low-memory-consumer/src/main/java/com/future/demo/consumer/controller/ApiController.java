package com.future.demo.consumer.controller;

import com.future.demo.common.feign.BpkcMxbFeign;
import feign.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

@RestController
@RequestMapping("/api/v1/external/bpkc-mxb")
public class ApiController {
    @Resource
    BpkcMxbFeign bpkcMxbFeign;

    @GetMapping("random")
    public void randomBpkcMxb(@RequestParam(value = "limit", defaultValue = "20000") Integer limit,
                              HttpServletResponse response) throws IOException {
        try (Response feignResponse = this.bpkcMxbFeign.random(limit)) {
            response.setStatus(feignResponse.status());
            Collection<String> contentTypes = feignResponse.headers().get("Content-Type");
            if (contentTypes != null && !contentTypes.isEmpty()) {
                response.setContentType(contentTypes.iterator().next());
            } else {
                response.setContentType("application/json;charset=UTF-8");
            }
            if (feignResponse.body() != null) {
                try (InputStream in = feignResponse.body().asInputStream()) {
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = in.read(buf)) != -1) {
                        response.getOutputStream().write(buf, 0, n);
                    }
                }
            }
        }
    }
}
