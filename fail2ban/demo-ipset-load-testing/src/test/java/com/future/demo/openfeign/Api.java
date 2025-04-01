package com.future.demo.openfeign;

import com.yyd.common.exception.BusinessException;
import com.yyd.common.http.response.ObjectResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;

import java.io.File;
import java.util.List;

public interface Api {
    @RequestLine("GET /")
    @Headers(value = {"x-forwarded-for: {xForwardedFor}"})
    String index(@Param("xForwardedFor") String xForwardedFor) throws BusinessException;
}
