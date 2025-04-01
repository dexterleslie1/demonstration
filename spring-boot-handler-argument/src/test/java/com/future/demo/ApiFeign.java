package com.future.demo;

import com.future.demo.argument.ParamMessageVO;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ApiFeign {
    @RequestLine("POST /api/v1/handler/argument/request/bodyWithObject")
    @Headers(value = {"Content-Type: application/json"})
    String bodyWithObject(@RequestBody(required = false) List<ParamMessageVO> paramMessageVOList) throws Exception;


    @RequestLine("POST /api/v1/handler/argument/testRequestParam1?contentList={contentList}")
    public String testRequestParam1(
            @Param("contentList") List<String> contentList) throws Exception;
}
