package com.future.demo.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 */
public abstract class BaseResponse {
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private int errorCode = 0;
    private String errorMessage = null;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        try {
            String JSON = OBJECT_MAPPER.writeValueAsString(this);
            return JSON;
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}
