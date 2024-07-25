package com.future.demo.uploader.controller;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

@RestController
@RequestMapping("/chunk")
@CrossOrigin(origins = "*")
public class ChunkController {
    @RequestMapping("/chunkUpload")
    public StdOut chunkUpload(MultipartFileParam param, HttpServletRequest request, HttpServletResponse response) {
        StdOut out = new StdOut();
        return out;

//        File file = new File(System.getProperty("java.io.tmpdir"));//存储路径
//
//        ChunkService chunkService = new ChunkService();
//
//        String path = file.getAbsolutePath();
//        response.setContentType("text/html;charset=UTF-8");
//
//        try {
//            //判断前端Form表单格式是否支持文件上传
//            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
//            if (!isMultipart) {
//                out.setCode(StdOut.PARAMETER_NULL);
//                out.setMessage("表单格式错误");
//                return out;
//            } else {
//                param.setTaskId(param.getIdentifier());
//                out.setModel(chunkService.chunkUploadByMappedByteBuffer(param, path));
//                return out;
//            }
//        } catch (NotSameFileExpection e) {
//            out.setCode(StdOut.FAIL);
//            out.setMessage("MD5校验失败");
//            return out;
//        } catch (Exception e) {
//            out.setCode(StdOut.FAIL);
//            out.setMessage("上传失败");
//            return out;
//        }
    }
}
