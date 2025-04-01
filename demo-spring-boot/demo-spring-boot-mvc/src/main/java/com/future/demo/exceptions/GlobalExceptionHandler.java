package com.future.demo.exceptions;

import com.future.common.constant.ErrorCodeConstant;
import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.stream.Collectors;

// 全局异常处理器
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // SpringBoot2需要以下配置处理NoHandlerFoundException异常
    // 处理404不存在资源异常
    /*@ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity<ObjectResponse<String>> handleNotFound(NoHandlerFoundException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        String message = "资源 " + e.getRequestURL() + " 不存在！";
        response.setErrorMessage(message);
        response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
    }*/

    // SpringBoot3需要以下配置处理NoResourceFoundException异常，https://github.com/spring-projects/spring-boot/issues/38733
    // 处理404不存在资源异常
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseBody
    public ResponseEntity<ObjectResponse<String>> handleNotFound(NoResourceFoundException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        String message = "资源 " + e.getResourcePath() + " 不存在！";
        response.setErrorMessage(message);
        response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // 处理空指针异常
    @ExceptionHandler(NullPointerException.class)
    public ObjectResponse<String> handleNullPointerException(NullPointerException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage("空指针异常");
        return response;
    }

    // 处理BusinessException异常
    @ExceptionHandler(BusinessException.class)
    public ObjectResponse<Object> handleBusinessException(BusinessException e) {
        ObjectResponse<Object> response = new ObjectResponse<>();
        response.setErrorCode(e.getErrorCode());
        response.setErrorMessage(e.getMessage());
        response.setData(e.getData());
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

    // 处理IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public @ResponseBody
    ResponseEntity<ObjectResponse<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
        response.setErrorMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // 处理MissingServletRequestParameterException
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public @ResponseBody
    ResponseEntity<ObjectResponse<String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
        String message = "缺失参数 \"" + e.getParameterName() + "\"！";
        response.setErrorMessage(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // 处理MethodArgumentTypeMismatchException
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public @ResponseBody
    ResponseEntity<ObjectResponse<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
        String message = "参数 " + e.getName() + " 值: " + String.valueOf(e.getValue()) + " 类型不匹配！";
        response.setErrorMessage(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // 处理404不存在资源异常
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity<ObjectResponse<String>> handleNotFound(NoHandlerFoundException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        String message = "资源 " + e.getRequestURL() + " 不存在！";
        response.setErrorMessage(message);
        response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // 处理其他异常
    /*@ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ObjectResponse<String>> handleException(
            HttpServletRequest request,
            Exception e) throws Exception {
        // AccessDeniedException和AuthenticationException不需要全局异常处理
        // AccessDeniedException由spring-security AccessDeniedHandler处理
        // AuthenticationException由spring-security AuthenticationEntryPoint处理
        //
        // https://stackoverflow.com/questions/72615257/spring-accessdeniedhandler-interface-dont-get-called-when-i-have-exceptionhandl
        // https://github.com/spring-projects/spring-security/issues/6908
        // https://github.com/Allurx/spring-security-demo/blob/master/src/main/java/red/zyc/security/handler/GlobalExceptionHandler.java
        if (e instanceof AccessDeniedException
                || e instanceof AuthenticationException) {
            throw e;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(";" + e.getMessage());

        String queryString = request.getQueryString();
        if (queryString != null) {
            builder.append(";").append(queryString);
        }

        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters != null && parameters.size() > 0) {
            builder.append(";params=");
            Set<String> keys = parameters.keySet();
            int size = keys.size();
            int counter = 0;
            for (String key : keys) {
                String[] arrayTemporary = parameters.get(key);
                builder.append(key + "=" + (arrayTemporary == null ? "" : String.join(",", arrayTemporary)));
                if (counter + 1 < size) {
                    builder.append(",");
                }
                counter++;
            }
        }
        log.error(builder.toString(), e);

        // 专门处理HystrixRuntimeException并且cause为FeignException异常
        if (e instanceof HystrixRuntimeException && (e.getCause() != null && e.getCause() instanceof FeignException)) {
            e = (FeignException) e.getCause();
        }

        int errorCode = ErrorCodeConstant.ErrorCodeCommon;
        String errorMessage;
        int httpStatus = HttpStatus.BAD_REQUEST.value();
        if (e instanceof ResourceAccessException) {
            errorMessage = "网络繁忙，稍后重试！";
        } else if (e instanceof FeignException) {
            FeignException feignException = (FeignException) e;
            httpStatus = feignException.status();
            ByteBuffer byteBuffer = feignException.responseBody().orElse(null);
            if (byteBuffer != null && byteBuffer.array().length > 0) {
                String resonseStr = new String(byteBuffer.array(), StandardCharsets.UTF_8);
                if (StringUtils.isBlank(resonseStr)) {
                    errorMessage = "网络繁忙，稍后重试！";
                } else {
                    try {
                        ObjectResponse<String> responseTemporary = JSONUtil.ObjectMapperInstance.readValue(resonseStr, new TypeReference<ObjectResponse<String>>() {
                        });
                        errorCode = responseTemporary.getErrorCode();
                        errorMessage = responseTemporary.getErrorMessage();
                    } catch (JsonProcessingException jsonProcessingException) {
                        errorMessage = "网络繁忙，稍后重试！";
                    }
                }
            } else {
                errorMessage = "网络繁忙，稍后重试！";
            }
        } else if (e instanceof HttpClientErrorException) {
            HttpClientErrorException httpClientErrorException = (HttpClientErrorException) e;
            httpStatus = httpClientErrorException.getRawStatusCode();
            String str = httpClientErrorException.getResponseBodyAsString(StandardCharsets.UTF_8);
            if (StringUtils.isBlank(str)) {
                errorMessage = "网络繁忙，稍后重试！";
            } else {
                try {
                    ObjectResponse<String> responseTemporary = JSONUtil.ObjectMapperInstance.readValue(str, new TypeReference<ObjectResponse<String>>() {
                    });
                    errorCode = responseTemporary.getErrorCode();
                    errorMessage = responseTemporary.getErrorMessage();
                } catch (JsonProcessingException jsonProcessingException) {
                    errorMessage = "网络繁忙，稍后重试！";
                }
            }
        } else {
            errorMessage = "网络繁忙，稍后重试！";
        }

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON).body(response);
    }*/

    // 处理文件上传大小超过服务器限制异常处理
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseEntity<ObjectResponse<String>> handleError(MaxUploadSizeExceededException e) {
        Throwable cause = e.getRootCause();
        String message;
        if (cause instanceof SizeLimitExceededException) {
            SizeLimitExceededException exception = (SizeLimitExceededException) cause;
            long permittedSize = exception.getPermittedSize();
            message = "上传文件总大小超过服务器限制，允许最大上传总大小" + String.valueOf(permittedSize / 1024 / 1024) + "MB！";
        } else if (cause instanceof FileSizeLimitExceededException) {
            FileSizeLimitExceededException exception = (FileSizeLimitExceededException) cause;
            long permittedSize = exception.getPermittedSize();
            String fileName = exception.getFileName();
            message = "上传文件 " + fileName + " 超过服务器限制，允许单个文件上传最大" + String.valueOf(permittedSize / 1024 / 1024) + "MB！";
        } else {
            message = "上传文件大小超过服务器限制！";
        }
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage(message);
        response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public @ResponseBody
    ResponseEntity<ObjectResponse<String>> handleConstraintViolationException(ConstraintViolationException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
        String message = e.getConstraintViolations().iterator().next().getMessage();

        if(log.isDebugEnabled())
            log.debug("参数校验失败，message={}", message);

        response.setErrorMessage(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
    }
}
