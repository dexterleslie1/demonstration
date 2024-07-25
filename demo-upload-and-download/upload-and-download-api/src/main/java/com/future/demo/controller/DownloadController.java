package com.future.demo.controller;

import com.future.demo.http.ObjectResponse;
import com.future.demo.util.Const;
import com.future.demo.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Enumeration;
import java.util.List;

@RestController
@RequestMapping(value="/api/v1/download")
@Slf4j
public class DownloadController {
    // 流式下载
    @GetMapping(value="stream/{filename}")
    public ResponseEntity<ObjectResponse<String>> stream(@PathVariable(value = "filename", required = false) String filename){
        if(StringUtils.isBlank(filename)) {
            throw new IllegalArgumentException("没有指定filename");
        }

        File temporaryDirectory = Util.getTemporaryDirectory();
        File fileToDownload = new File(temporaryDirectory, Const.DemoDirectoryName + File.separator + Const.DirectoryUploaded + File.separator + filename);
        if(!fileToDownload.exists()) {
            String message = String.format("文件 %s 不存在", filename);
            throw new IllegalArgumentException(message);
        }

        long fileLength = fileToDownload.length();
        ResponseEntity responseEntity = ResponseEntity.ok()
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+filename+"\"")
                .body(new FileSystemResource(fileToDownload));
        return responseEntity;
    }

    // 范围下载
    // HTTP range用法
    // https://www.cnblogs.com/1995hxt/p/5692050.html
    // https://www.jianshu.com/p/acca9656e250
    // https://blog.csdn.net/hechaojie_com/article/details/81989951
    @GetMapping(value="range/{filename}")
    public ResponseEntity<StreamingResponseBody> range(
            @PathVariable(value = "filename", required = false) String filename,
            @RequestHeader(value = "Range", required = false) String range,
            HttpServletRequest request){
        if(StringUtils.isBlank(filename)) {
            throw new IllegalArgumentException("没有指定filename");
        }

        // 打印所有请求头
        StringBuilder builderHeaderString = new StringBuilder();
        builderHeaderString.append("请求头，");
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            builderHeaderString.append(headerName + ": " + headerValue + ";");
        }
        log.debug(builderHeaderString.toString());

        File temporaryDirectory = Util.getTemporaryDirectory();
        File fileToDownload = new File(temporaryDirectory, Const.DemoDirectoryName + File.separator + Const.DirectoryUploaded + File.separator + filename);
        if(!fileToDownload.exists()) {
            String message = String.format("文件 %s 不存在", filename);
            throw new IllegalArgumentException(message);
        }

        if(!StringUtils.isBlank(range)) {
            range = range.replace("bytes=", "");
        }
        long contentLength = fileToDownload.length();
        HttpStatus httpStatus = HttpStatus.OK;
        String contentRange = null;
        long start = 0;
        long end = contentLength-1;
        if(!StringUtils.isBlank(range)) {
            httpStatus = HttpStatus.PARTIAL_CONTENT;
            start = Long.parseLong(range.split("-")[0]);
            end = range.split("-").length==1?contentLength-1:Long.parseLong(range.split("-")[1]);
            contentRange = "bytes " + start + "-" + end + "/" + contentLength;
        }

        final long startFinal = start;
        final long endFinal = end;
        final String contentRangeConst = contentRange;
        StreamingResponseBody streamingResponseBody = new StreamingResponseBody() {
            @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(fileToDownload);
//                    StreamUtils.copyRange(inputStream, outputStream, startFinal, endFinal);

                    long skipped = inputStream.skip(startFinal);
                    if (skipped < startFinal) {
                        throw new IOException("Skipped only " + skipped + " bytes out of " + startFinal + " required");
                    }

                    long bytesToCopy = endFinal - startFinal + 1;
                    byte[] buffer = new byte[(int) Math.min(StreamUtils.BUFFER_SIZE, bytesToCopy)];
                    while (bytesToCopy > 0) {
                        Thread.sleep(10);

                        int bytesRead = inputStream.read(buffer);
                        if (bytesRead == -1) {
                            break;
                        }
                        else if (bytesRead <= bytesToCopy) {
                            outputStream.write(buffer, 0, bytesRead);
                            bytesToCopy -= bytesRead;
                        }
                        else {
                            outputStream.write(buffer, 0, (int) bytesToCopy);
                            bytesToCopy = 0;
                        }
                    }

                    log.debug("成功下载文件 {},content-range={}", fileToDownload.getAbsolutePath(), contentRangeConst);
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
                .contentLength(end-start+1)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+filename+"\"");

        if(!StringUtils.isBlank(contentRange)) {
            builder.header("Content-Range", contentRange);
        }

        ResponseEntity responseEntity = builder.body(streamingResponseBody);
        return responseEntity;
    }
}
