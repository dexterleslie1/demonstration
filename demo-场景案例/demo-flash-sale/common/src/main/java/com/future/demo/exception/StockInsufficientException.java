package com.future.demo.exception;

import com.future.common.exception.BusinessException;

public class StockInsufficientException extends BusinessException {
    public StockInsufficientException(String errorMessage) {
        super(errorMessage);
    }

    public StockInsufficientException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
