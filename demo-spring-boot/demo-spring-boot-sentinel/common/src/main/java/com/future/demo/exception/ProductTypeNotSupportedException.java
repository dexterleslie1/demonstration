package com.future.demo.exception;

import com.future.common.exception.BusinessException;

public class ProductTypeNotSupportedException extends BusinessException {
    public ProductTypeNotSupportedException(String errorMessage) {
        super(errorMessage);
    }

    public ProductTypeNotSupportedException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
