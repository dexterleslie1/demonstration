package com.future.demo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = OAuth2ExceptionJsonSerializer.class)
public class OAuth2ExceptionWithCustomizeJson extends Exception {
    public OAuth2ExceptionWithCustomizeJson(String msg) {
        super(msg);
    }
}
