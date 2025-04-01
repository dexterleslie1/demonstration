package com.future.demo.exception;

import java.io.IOException;

/**
 * 业务逻辑抛出异常
 */
public class BusinessException extends IOException /* NOTE: 需要从IOException派生，因为有些方法需要抛出IOException类型异常 */ {
    private int errorCode = 0;
    private String errorMessage = null;
    private Object data = null;


    /**
     *
     * @param errorMessage
     */
    public BusinessException(String errorMessage){
        this(600, errorMessage);
    }

    /**
     *
     * @param errorCode
     * @param errorMessage
     */
    public BusinessException(int errorCode, String errorMessage){
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     *
     * @return
     */
    public int getErrorCode(){
        return this.errorCode;
    }

    /**
     *
     * @return
     */
    public String getErrorMessage(){
        return this.errorMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
