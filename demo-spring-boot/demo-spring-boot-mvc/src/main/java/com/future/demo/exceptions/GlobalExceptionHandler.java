package com.future.demo.exceptions;

import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

// 全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理空指针异常
    @ExceptionHandler(NullPointerException.class)
    public ObjectResponse<String> handleNullPointerException(NullPointerException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage("空指针异常");
        return response;
    }

    // 处理BusinessException异常
    @ExceptionHandler(BusinessException.class)
    public ObjectResponse<String> handleBusinessException(BusinessException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage(e.getMessage());
        return response;
    }

    // 处理spring数据校验失败异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ObjectResponse<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ObjectResponse<Map<String, String>> response = new ObjectResponse<>();
        response.setErrorMessage("参数校验失败");
        Map<String, String> map = e.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        response.setData(map);
        return response;
    }
}
