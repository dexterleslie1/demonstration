package com.future.demo.controller;

import com.future.demo.http.ListResponse;
import com.future.demo.http.ObjectResponse;
import com.future.demo.util.Const;
import com.future.demo.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;

@RestController
@RequestMapping(value="/api/v1/extra")
@Slf4j
public class ExtraApiController {
    // 删除指定的已上传文件
    @PutMapping(value = "deleteUploaded")
    public ObjectResponse<String> deleteUploaded(@RequestParam(value = "filename", defaultValue = "") String filename) {
        if(StringUtils.isEmpty(filename)) {
            throw new IllegalArgumentException("没有指定文件");
        }
        File temporaryDirectory = Util.getTemporaryDirectory();
        File file = new File(temporaryDirectory, Const.DemoDirectoryName + File.separator + Const.DirectoryUploaded + File.separator + filename);
        if(file.exists()) {
            file.delete();
            log.debug("成功删除文件 {}", file.getAbsolutePath());
        } else {
            log.debug("文件 {} 不存在", file.getAbsolutePath());
        }

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功删除文件 " + filename);
        return response;
    }

    // 查询已上传文件列表
    @GetMapping(value="listUploaded")
    public ListResponse<Map<String, String>> listUploaded(){
        File temporaryDirectory = Util.getTemporaryDirectory();
        File uploadedDirectory = new File(temporaryDirectory, Const.DemoDirectoryName + File.separator + Const.DirectoryUploaded);

        File []files = uploadedDirectory.listFiles();

        ListResponse<Map<String, String>> response = new ListResponse<>();
        if(files!=null && files.length>0) {
            List<Map<String, String>> listReturn = new ArrayList<>();
            for(File file : files) {
                if(!file.isFile()) {
                    continue;
                }
                Map<String, String> mapTemporary = new HashMap<>();
                String filename = file.getName();
                String unit = "bytes";
                long length = file.length();
                if(length>=1024) {
                    length = length/1024;
                    unit = "KB";
                }
                mapTemporary.put("filename", filename);
                mapTemporary.put("size", length + unit);
                listReturn.add(mapTemporary);
            }
            response.setData(listReturn);
        }
        return response;
    }

    // 查询已上传图片文件
    @GetMapping(value="listUploadedImages")
    public ListResponse<Map<String, String>> listUploadedImages(){
        File temporaryDirectory = Util.getTemporaryDirectory();
        File uploadedDirectory = new File(temporaryDirectory, Const.DemoDirectoryName + File.separator + Const.DirectoryUploaded);

        File []files = uploadedDirectory.listFiles();

        ListResponse<Map<String, String>> response = new ListResponse<>();
        if(files!=null && files.length>0) {
            List<Map<String, String>> listReturn = new ArrayList<>();
            for(File file : files) {
                String filename = file.getName();
                if(!file.isFile() || !(filename.endsWith("jpg") || filename.endsWith("png") || filename.endsWith("jpeg"))) {
                    continue;
                }
                Map<String, String> mapTemporary = new HashMap<>();
                String unit = "bytes";
                long length = file.length();
                if(length>=1024) {
                    length = length/1024;
                    unit = "KB";
                }
                mapTemporary.put("filename", filename);
                mapTemporary.put("size", length + unit);
                listReturn.add(mapTemporary);
            }
            response.setData(listReturn);
        }
        return response;
    }
}
