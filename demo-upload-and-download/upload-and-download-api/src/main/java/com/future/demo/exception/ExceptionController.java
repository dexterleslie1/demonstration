package com.future.demo.exception;

import ch.qos.logback.core.util.FileSize;
import com.future.demo.http.ObjectResponse;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@ControllerAdvice
public class ExceptionController {
	
	private final static Logger log = LoggerFactory.getLogger(ExceptionController.class);
	
    /**
     *
     * @param e
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public @ResponseBody
    ResponseEntity<ObjectResponse<String>> handleIllegalArgumentException(IllegalArgumentException e)   {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(600);
        response.setErrorMessage(e.getMessage());
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public @ResponseBody
    ResponseEntity<ObjectResponse<String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e)   {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(600);
        response.setErrorMessage("Api调用缺失参数：" + e.getParameterName());
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public @ResponseBody
    ResponseEntity<ObjectResponse<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e)   {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(600);
        response.setErrorMessage("Api调用参数转换错误，参数：" + e.getName() + "，值：" + e.getValue());
        return ResponseEntity.ok(response);
    }

    /**
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public @ResponseBody ResponseEntity<ObjectResponse<String>> handleBusinessException(BusinessException e)   {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(e.getErrorCode());
        response.setErrorMessage(e.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     *
     * @param e
     * @return
     */
    @ExceptionHandler(NoHandlerFoundException.class)
//    @ResponseStatus(value=HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<ObjectResponse<String>> handleError(NoHandlerFoundException e)   {
    	log.error(e.getMessage(),e);
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage("访问资源 " + e.getRequestURL() + " 不存在");
        response.setErrorCode(600);
        return ResponseEntity.ok(response);
    }

    // 文件上传大小超过服务器限制异常处理
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseEntity<ObjectResponse<String>> handleError(MaxUploadSizeExceededException e)   {
        Throwable cause = e.getRootCause();
        String message;
        if(cause instanceof SizeLimitExceededException) {
           SizeLimitExceededException exception = (SizeLimitExceededException)cause;
           long permittedSize = exception.getPermittedSize();
           message = "上传文件总大小超过服务器限制，允许最大上传总大小" + permittedSize/1024/1024 + "MB";
        } else if(cause instanceof FileSizeLimitExceededException) {
            FileSizeLimitExceededException exception = (FileSizeLimitExceededException)cause;
            long permittedSize = exception.getPermittedSize();
            String fileName = exception.getFileName();
            message = "上传文件 \"" + fileName + "\" 超过服务器限制，允许单个文件上传最大大小" + permittedSize/1024/1024 + "MB";
        } else {
            log.error(e.getMessage(), e);
            message = "上传文件大小超过服务器限制";
        }
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage(message);
        response.setErrorCode(600);
        return ResponseEntity.ok(response);
    }

    /**
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ObjectResponse<String>> handleException(
            HttpServletRequest request ,
            Exception e)   {
        StringBuilder builder = new StringBuilder();
        builder.append(";" + e.getMessage());

        String queryString = request.getQueryString();
        if (queryString != null) {
            builder.append(";").append(queryString);
        }

        Map<String, String []> parameters = request.getParameterMap();
        if(parameters != null && parameters.size()>0) {
            builder.append(";params=");
            Set<String> keys = parameters.keySet();
            int size = keys.size();
            int counter = 0;
            for (String key : keys) {
                String [] arrayTemporary = parameters.get(key);
                builder.append(key + "=" + (arrayTemporary==null?"":String.join(",", arrayTemporary)));
                if(counter+1<size){
                    builder.append(",");
                }
                counter++;
            }
        }
    	log.error(builder.toString(),e);
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(600);
        if(e instanceof ResourceAccessException){
            response.setErrorMessage("网络繁忙，稍后重试");
        }else{
            response.setErrorMessage("网络繁忙，稍后重试");
        }
        return ResponseEntity.ok(response);
    }

}
