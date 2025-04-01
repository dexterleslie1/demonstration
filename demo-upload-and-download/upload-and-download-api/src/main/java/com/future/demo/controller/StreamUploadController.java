//package com.future.demo.controller;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//
//// 流式上传
//@RestController
//@RequestMapping(value="/api/v1/stream")
//@Slf4j
//public class StreamUploadController {
//    @PostMapping(value = "upload")
//    public ResponseEntity<String> upload(@RequestParam(value = "file", required = false) MultipartFile[] files) throws IOException {
//        if(files==null || files.length<=0) {
//            return ResponseEntity.ok("没有指定上传文件");
//        }
//
//        log.debug("上传文件总数:{}", files.length);
//
//        for(MultipartFile fileTemporary : files) {
//            File tempFile = File.createTempFile("upload", ".tmp");
//            fileTemporary.transferTo(tempFile);
//
//            String name = fileTemporary.getName();
//            String originalFilename = fileTemporary.getOriginalFilename();
//            String contentType = fileTemporary.getContentType();
//            long size = fileTemporary.getSize();
//
//            log.debug("上传文件 提交表单名:{}, 文件名称:{}, 文件类型:{}, 大小:{}字节, 临时保存路径:{}"
//                    , name, originalFilename, contentType, size, tempFile.getAbsolutePath());
//        }
//
//        return ResponseEntity.ok("成功上传" + files.length + "个文件");
//    }
//
////    /**
////     * 下载文件
////     * @return
////     */
////    @PostMapping(value = "download")
////    public ResponseEntity download(){
////        FileOutputStream fileOutputStream=null;
////        FileInputStream fileInputStream=null;
////        ResponseEntity responseEntity=null;
////        try{
////            File file = createFileIfNotExists();
////
////            fileInputStream=new FileInputStream(file);
////            responseEntity=ResponseEntity.ok()
////                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
////                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
////                    .body(new InputStreamResource(fileInputStream));
////        } catch(Exception ex) {
////            ex.printStackTrace();
////            responseEntity=ResponseEntity.ok()
////                    .contentType(MediaType.TEXT_PLAIN)
////                    .body(ex.getMessage());
////            return responseEntity;
////        } finally {
////            try {
////                if (fileOutputStream != null) {
////                    fileOutputStream.close();
////                    fileOutputStream = null;
////                }
////            }catch(IOException ex){
////            }
////        }
////        return responseEntity;
////    }
////
////    final static String TemporaryDirectory=System.getProperty("java.io.tmpdir");
////    File createFileIfNotExists() throws IOException {
////        String filename = TemporaryDirectory + "spring-boot-upload-download.tmp";
////        File file = new File(filename);
////        if(!file.exists()) {
////            file.createNewFile();
////            FileOutputStream outputStream = null;
////            try {
////                outputStream = new FileOutputStream(file);
////                outputStream.write("测试文件".getBytes("utf-8"));
////            } catch (Exception ex) {
////                throw ex;
////            } finally {
////                if (outputStream != null) {
////                    outputStream.close();
////                    outputStream = null;
////                }
////            }
////        }
////        return file;
////    }
//}
