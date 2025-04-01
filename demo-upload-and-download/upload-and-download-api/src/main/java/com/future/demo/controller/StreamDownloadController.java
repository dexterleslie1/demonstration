//package com.future.demo.controller;
//
//import com.future.demo.http.ObjectResponse;
//import com.future.demo.util.Util;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.File;
//
//// 流式下载
//@RestController
//@RequestMapping(value="/api/v1/stream")
//@Slf4j
//public class StreamDownloadController {
//    @GetMapping(value="download/{filename}")
//    public ResponseEntity<ObjectResponse<String>> download(@PathVariable(value = "filename", required = false) String filename){
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
//        long fileLength = fileToDownload.length();
//        ResponseEntity responseEntity = ResponseEntity.ok()
//                .contentLength(fileLength)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+filename+"\"")
//                .body(new FileSystemResource(fileToDownload));
//        return responseEntity;
//    }
//}
