package com.future.demo;

import com.future.common.http.ObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/v1")
@Slf4j
public class ApiController {
    /**
     * 文件上传
     *
     * @param token 用于协助zuul filter提供token参数测试
     * @param files
     * @return
     * @throws IOException
     */
    @PostMapping(value = "upload")
    public ObjectResponse<String> upload(
            @RequestHeader(value = "token", required = false) String token,
            @RequestParam(value = "file", required = false) MultipartFile[] files) throws IOException {
        Assert.isTrue(!StringUtils.isBlank(token), "请提供token参数");
        if (files == null || files.length <= 0) {
            throw new IllegalArgumentException("没有指定上传文件");
        }

        List<MultipartFile> multipartFiles = new ArrayList<>();
        for (MultipartFile fileTemporary : files) {
            if (!fileTemporary.isEmpty()) {
                multipartFiles.add(fileTemporary);
            }
        }

        log.debug("上传文件总数:{}", multipartFiles.size());

        File temporaryDirectory = getTemporaryDirectory();
        for (MultipartFile fileTemporary : multipartFiles) {
            if (fileTemporary.isEmpty()) {
                continue;
            }

            File folder = new File(temporaryDirectory, "/uploaded");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String originalFilename = fileTemporary.getOriginalFilename();
            File file = new File(temporaryDirectory, "/uploaded/" + originalFilename);
            fileTemporary.transferTo(file);

            String name = fileTemporary.getName();
            String contentType = fileTemporary.getContentType();
            long size = fileTemporary.getSize();

            log.debug("上传文件 提交表单名:{}, 文件名称:{}, 文件类型:{}, 大小:{}字节, 临时保存路径:{}"
                    , name, originalFilename, contentType, size, file.getAbsolutePath());
        }

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功上传" + multipartFiles.size() + "个文件");
        return response;
    }

    /**
     * 文件下载
     *
     * @param filename
     * @return
     */
    @GetMapping(value = "download/{filename}")
    public ResponseEntity<ObjectResponse<String>> download(@PathVariable(value = "filename", required = false) String filename) {
        if (StringUtils.isBlank(filename)) {
            throw new IllegalArgumentException("没有指定filename");
        }

        File temporaryDirectory = getTemporaryDirectory();
        File fileToDownload = new File(temporaryDirectory, "/uploaded/" + filename);
        if (!fileToDownload.exists()) {
            String message = String.format("文件 %s 不存在", filename);
            throw new IllegalArgumentException(message);
        }

        long fileLength = fileToDownload.length();
        ResponseEntity responseEntity = ResponseEntity.ok()
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(new FileSystemResource(fileToDownload));
        return responseEntity;
    }

    static File getTemporaryDirectory() {
        String temporaryDirectory = System.getProperty("java.io.tmpdir");
        return new File(temporaryDirectory);
    }
}
