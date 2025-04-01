package com.future.demo.test.client;

import feign.Headers;
import feign.RequestLine;

import java.util.Map;


public interface Api {
    @RequestLine("POST /default512k")
    @Headers(value = {"Content-Type: application/json"})
    String limitDefault512k(Map<String, String> parameters);

    @RequestLine("POST /128k")
    @Headers(value = {"Content-Type: application/json"})
    String limit128k(Map<String, String> parameters);

    @RequestLine("POST /1m")
    @Headers(value = {"Content-Type: application/json"})
    String limit1m(Map<String, String> parameters);
}
