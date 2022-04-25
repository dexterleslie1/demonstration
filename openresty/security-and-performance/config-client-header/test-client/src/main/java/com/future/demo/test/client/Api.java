package com.future.demo.test.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface Api {
    @RequestLine("POST /{requestUri}")
    String testRequestUri(@Param("requestUri") String requestUri);

    @Headers("my-header-field: {headerField}")
    @RequestLine("POST /")
    String testHeaderField(@Param("headerField") String headerField);

    @Headers({"my-header-field1: {headerField}",
            "my-header-field2: {headerField}",
            "my-header-field3: {headerField}",
            "my-header-field4: {headerField}",
            "my-header-field5: {headerField}"})
    @RequestLine("POST /{requestUri}")
    String testRequestUriHeaderFieldExcceed(@Param("requestUri") String requestUri,
                                            @Param("headerField") String headerField);
}
