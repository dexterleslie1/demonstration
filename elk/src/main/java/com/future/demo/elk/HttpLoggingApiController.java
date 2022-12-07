package com.future.demo.elk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 用于调试http logging相关api
 *
 * springBoot打印POST请求原始入参body体
 * https://blog.csdn.net/flyfeifei66/article/details/104950618
 */
@RestController
public class HttpLoggingApiController {
    final static Logger log = LoggerFactory.getLogger(HttpLoggingApiController.class);
    final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @GetMapping(value = "/api/v1/testWithParam")
    public ResponseEntity<String> testWithParam(@RequestParam(value = "param1", defaultValue = "") String param1) throws Exception {
        return ResponseEntity.ok().body("param1=" + param1);
    }

    @PostMapping(value = "/api/v1/testWithParamAndBody")
    public ResponseEntity<String> testWithParamAndBody(
            @RequestParam(value = "param1", required = false) String param1,
            @RequestBody(required = false) List<Foo> fooList) throws JsonProcessingException {
        String responseJSON = OBJECT_MAPPER.writeValueAsString(fooList);
        String resposneStr = "param1=" + param1 + ",body=" + responseJSON;
        return ResponseEntity.ok().body(resposneStr);
    }

    @PostMapping(value = "/api/v1/testUpload")
    public ResponseEntity<String> testUpload(
            @RequestParam(value = "param1", required = false) String param1,
            @RequestParam(value = "file", required = false) MultipartFile[] multipartFiles) {
        String resonseStr;
        if(multipartFiles==null || multipartFiles.length<=0) {
            resonseStr = "param1=" + param1 + ",没有指定文件";
        } else {
            resonseStr = "param1=" + param1 + ",上传文件originalName=" + multipartFiles[0].getOriginalFilename();
        }
        return ResponseEntity.ok().body(resonseStr);
    }

    @GetMapping(value = "/api/v1/testDownload1")
    public ResponseEntity<String> testDownload1() throws Exception {
        String filename = UUID.randomUUID().toString();
        byte bytes[] = new byte[1024*1024];
        Random random = new Random();
        random.nextBytes(bytes);
        long fileLength = bytes.length;

        ResponseEntity responseEntity = ResponseEntity.ok()
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+filename+"\"")
                .body(new ByteArrayResource(bytes));
        return responseEntity;
    }

    @GetMapping(value = "/api/v1/testDownload2")
    public ResponseEntity<StreamingResponseBody> testDownload2() throws Exception {
        String filename = UUID.randomUUID().toString();
        byte bytes[] = new byte[1024*1024];
        Random random = new Random();
        random.nextBytes(bytes);
        long contentLength = bytes.length;

        HttpStatus httpStatus = HttpStatus.OK;
        StreamingResponseBody streamingResponseBody = new StreamingResponseBody() {
            @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                InputStream inputStream = null;
                try {
                    inputStream = new ByteArrayInputStream(bytes);
                    StreamUtils.copy(inputStream, outputStream);
                } catch (Exception ex) {
                    if(!(ex instanceof ClientAbortException)) {
                        log.error(ex.getMessage(), ex);
                    }
                } finally {
                    if(inputStream!=null) {
                        inputStream.close();
                        inputStream = null;
                    }
                }
            }
        };

        ResponseEntity.BodyBuilder builder = ResponseEntity.status(httpStatus)
                // 必须要指定文件大小，否则浏览器下载文件时不能正确显示下载进度和文件总大小
                .contentLength(contentLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+filename+"\"");

        ResponseEntity responseEntity = builder.body(streamingResponseBody);
        return responseEntity;
    }
}
