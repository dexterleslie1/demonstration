package com.future.demo;

import com.future.common.exception.BusinessException;
import com.future.common.http.ListResponse;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@Validated
public class ApiController {
    final static String TemporaryDirectoryPath = System.getProperty("java.io.tmpdir");
    @Value("${spring.application.name}")
    String applicationName;

    // 演示axios put调用
    @PutMapping("putWithBody")
    ObjectResponse<String> putWithBody(
            @RequestBody(required = false) LoginForm loginForm,
            @RequestHeader(value = "header1", defaultValue = "") String header1,
            @RequestParam(value = "param1", defaultValue = "") String param1) {
        Assert.isTrue(loginForm != null, "没有提供登录参数");
        Assert.isTrue(StringUtils.hasText(loginForm.getUsername()), "没有提供username参数");
        Assert.isTrue(StringUtils.hasText(loginForm.getPassword()), "没有提供password参数");
        Assert.isTrue(StringUtils.hasText(loginForm.getVerificationCode()), "没有提供验证码参数");
        Assert.isTrue(StringUtils.hasText(header1), "没有提供header1参数");
        Assert.isTrue(StringUtils.hasText(param1), "没有提供param1参数");
        log.debug("username={},password={},verificationCode={},header1={},param1={}",
                loginForm.getUsername(),
                loginForm.getPassword(),
                loginForm.getVerificationCode(),
                header1,
                param1);
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("username=" + loginForm.getUsername() + ",password=" + loginForm.getPassword()
                + ",verificationCode=" + loginForm.getVerificationCode() + ",header1=" + header1
                + ",param1=" + param1);
        return response;
    }

    // 演示axios post调用
    @PostMapping("postWithBody")
    ObjectResponse<String> postWithBody(
            @RequestBody(required = false) LoginForm loginForm,
            @RequestHeader(value = "header1", defaultValue = "") String header1,
            @RequestParam(value = "param1", defaultValue = "") String param1) {
        Assert.isTrue(loginForm != null, "没有提供登录参数");
        Assert.isTrue(StringUtils.hasText(loginForm.getUsername()), "没有提供username参数");
        Assert.isTrue(StringUtils.hasText(loginForm.getPassword()), "没有提供password参数");
        Assert.isTrue(StringUtils.hasText(loginForm.getVerificationCode()), "没有提供验证码参数");
        Assert.isTrue(StringUtils.hasText(header1), "没有提供header1参数");
        Assert.isTrue(StringUtils.hasText(param1), "没有提供param1参数");
        log.debug("username={},password={},verificationCode={},header1={},param1={}",
                loginForm.getUsername(),
                loginForm.getPassword(),
                loginForm.getVerificationCode(),
                header1,
                param1);
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("username=" + loginForm.getUsername() + ",password=" + loginForm.getPassword()
                + ",verificationCode=" + loginForm.getVerificationCode() + ",header1=" + header1
                + ",param1=" + param1);
        return response;
    }

    // 演示axios delete调用
    @DeleteMapping("delete")
    ObjectResponse<String> delete(
            @RequestParam(value = "param1", defaultValue = "") String param1,
            @RequestHeader(value = "header1", defaultValue = "") String header1) {
        Assert.isTrue(StringUtils.hasText(header1), "没有提供header1参数");
        Assert.isTrue(StringUtils.hasText(param1), "没有提供param1参数");
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("param1=" + param1 + ",header1=" + header1);
        return response;
    }

    // 演示axios get调用
    @GetMapping("get")
    ObjectResponse<String> get(
            @RequestParam(value = "param1", defaultValue = "") String param1,
            @RequestHeader(value = "header1", defaultValue = "") String header1,
            @RequestHeader(value = "header2", defaultValue = "") String header2) {
        Assert.isTrue(StringUtils.hasText(header1), "没有提供header1参数");
        Assert.isTrue(StringUtils.hasText(param1), "没有提供param1参数");
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("param1=" + param1 + ",header1=" + header1 + ",header2=" + header2);
        return response;
    }

    // 演示axios get返回text/plain
    @GetMapping(value = "1.txt", produces = {MediaType.TEXT_PLAIN_VALUE})
    String getTextPlain(
            @RequestParam(value = "param1", defaultValue = "") String param1,
            @RequestHeader(value = "header1", defaultValue = "") String header1) {
        return "param1=" + param1 + ",header1=" + header1;
    }

    /**
     * 测试使用 Axios 上传文件
     *
     * @param multipartFiles
     * @return
     * @throws IOException
     */
    @PostMapping("postWithFileUpload")
    ListResponse<String> postWithFileUpload(
            @NotNull(message = "请指定上传的文件")
            @Size(min = 1, message = "请指定上传的文件")
            @RequestParam(value = "files", required = false) MultipartFile[] multipartFiles) throws IOException {
        // 过滤空的 MultipartFile
        List<MultipartFile> temporaryMultipartList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                temporaryMultipartList.add(multipartFile);
            }
        }
        multipartFiles = temporaryMultipartList.toArray(new MultipartFile[0]);

        List<File> fileList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            // 上传时的文件名称
            String originalFilename = multipartFile.getOriginalFilename();
            // 获取文件的扩展名
            String filenameExtension = FilenameUtils.getExtension(originalFilename);
            // 在 /tmp 目录创建临时文件
            String filePath = TemporaryDirectoryPath + File.separator + UUID.randomUUID() + "." + filenameExtension;
            File fileTemporary = new File(filePath);
            boolean created = fileTemporary.createNewFile();
            if (!created) {
                if (log.isWarnEnabled()) {
                    log.warn("创建文件失败，路径 {}", fileTemporary.getAbsolutePath());
                }
                throw new BusinessException("文件转换失败！");
            }

            if (log.isDebugEnabled()) {
                log.debug("创建文件成功，原始文件 {}，新文件路径 {}", originalFilename, fileTemporary.getAbsolutePath());
            }

            multipartFile.transferTo(fileTemporary);
            fileList.add(fileTemporary);
            if (log.isDebugEnabled()) {
                log.debug("原始文件 {} 被成功转移到 {}", originalFilename, fileTemporary.getAbsolutePath());
            }
        }

        List<String> filenameList = fileList.stream().map(File::getName).collect(Collectors.toList());
        return ResponseUtils.successList(filenameList);
    }

    @GetMapping("{filename}")
    ResponseEntity downloadFile(
            @NotNull(message = "请指定下载的文件")
            @NotBlank(message = "请指定下载的文件")
            @PathVariable(value = "filename", required = false) String filename) {
        File file = new File(TemporaryDirectoryPath + File.separator + filename);
        long fileLength = file.length();

        ResponseEntity responseEntity = ResponseEntity.ok()
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(new FileSystemResource(file));
        return responseEntity;
    }
}
