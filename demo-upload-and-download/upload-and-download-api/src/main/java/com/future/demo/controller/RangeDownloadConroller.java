//package com.future.demo.controller;
//
//import com.future.demo.http.ObjectResponse;
//import com.future.demo.util.Util;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.StreamUtils;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.nio.file.Files;
//
//// 范围下载
////
//// HTTP range用法
//// https://www.cnblogs.com/1995hxt/p/5692050.html
//// https://www.jianshu.com/p/acca9656e250
//@RestController
//@RequestMapping(value="/api/v1/range")
//@Slf4j
//public class RangeDownloadConroller {
//    @GetMapping(value="download/{filename}")
//    public ResponseEntity<StreamingResponseBody> download(@PathVariable(value = "filename", required = false) String filename,
//                                                          @RequestHeader(value = "Range", required = false) String range){
//
//        if(StringUtils.isEmpty(filename)) {
//            throw new IllegalArgumentException("没有指定filename");
//        }
//
//        File temporaryDirectory = Util.getTemporaryDirectory();
//        File fileToDownload = new File(temporaryDirectory, filename);
//        if(!fileToDownload.exists()) {
//            String message = String.format("文件 %s 不存在", filename);
//            throw new IllegalArgumentException(message);
//        }
//
//        if(!StringUtils.isEmpty(range)) {
//            range = range.replace("bytes=", "");
//        }
//        long contentLength = fileToDownload.length();
//        HttpStatus httpStatus = HttpStatus.OK;
//        String contentRange = null;
//        long start = 0;
//        long end = contentLength-1;
//        if(!StringUtils.isEmpty(range)) {
//            httpStatus = HttpStatus.PARTIAL_CONTENT;
//            start = Long.parseLong(range.split("-")[0]);
//            end = range.split("-").length==1?contentLength:Long.parseLong(range.split("-")[1]);
//            contentRange = "bytes " + start + "-" + end + "/" + contentLength;
//        }
//
//        final long startFinal = start;
//        final long endFinal = end;
//        StreamingResponseBody streamingResponseBody = new StreamingResponseBody() {
//            @Override
//            public void writeTo(OutputStream outputStream) throws IOException {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    //
//                }
//                StreamUtils.copyRange(new FileInputStream(fileToDownload), outputStream, startFinal, endFinal);
//                log.debug("成功下载文件 {}", fileToDownload.getAbsolutePath());
//            }
//        };
//
//        ResponseEntity.BodyBuilder builder = ResponseEntity.status(httpStatus)
//                // 必须要指定文件大小，否则浏览器下载文件时不能正确显示下载进度和文件总大小
//                .contentLength(end-start+1)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+filename+"\"");
//
//        if(!StringUtils.isEmpty(contentRange)) {
//            builder.header("Content-Range", contentRange);
//        }
//
//        ResponseEntity responseEntity = builder.body(streamingResponseBody);
//        return responseEntity;
//    }
//}
