package com.future.demo.controller;


import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exception")
public class ExceptionController {

    // 抛出算数术异常
    @RequestMapping("test1")
    public ObjectResponse<String> throwArithmeticException() {
        throw new ArithmeticException("算数异常");
    }

    // 抛出空指针异常
    @RequestMapping("test2")
    public ObjectResponse<String> throwNullPointerException() {
        throw new NullPointerException("空指针异常");
    }

    // 抛出自定义异常
    @RequestMapping("test3")
    public ObjectResponse<String> throwBusinessException() throws BusinessException {
        throw new BusinessException("自定义异常");
    }

    // 抛出其他异常
    /*@RequestMapping("test4")
    public ObjectResponse<String> throwException() throws Exception {
        throw new Exception("其他异常");
    }*/

    // 本controller处理算数术异常
    @ExceptionHandler(ArithmeticException.class)
    public ObjectResponse<String> handleArithmeticException(ArithmeticException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage(e.getMessage());
        return response;
    }

    // 处理其他没有被处理的异常
    /*@ExceptionHandler(Throwable.class)
    public ObjectResponse<String> handleThrowable(Throwable e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage("其他没有被处理的异常");
        return response;
    }*/
}
