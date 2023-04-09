package com.future.demo.filter;

import com.yyd.common.http.response.ObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class ApiController {
    // https://stackoverflow.com/questions/33796218/content-type-application-x-www-form-urlencodedcharset-utf-8-not-supported-for
    @PostMapping("postWwwFormUrlencoded")
    public ObjectResponse<String> postWwwFormUrlencoded(MyPostVO myPostVO) {
        String param1 = myPostVO.getParameter1();
        Assert.isTrue(!StringUtils.isBlank(param1), "没有指定param1参数");
        ObjectResponse<String> response = new ObjectResponse<>();
        if(StringUtils.isBlank(myPostVO.getParameter2())) {
            response.setData("提交参数parameter1=" + param1);
        } else {
            response.setData("提交参数parameter1=" + param1 + ",parameter2=" + myPostVO.getParameter2());
        }
        return response;
    }

    @DeleteMapping("delete")
    public ObjectResponse<String> delete(@RequestParam(value = "param1", defaultValue = "") String param1,
                                         @RequestParam(value = "parameter2", defaultValue = "") String parameter2) {
        Assert.isTrue(!StringUtils.isBlank(param1), "没有指定param1参数");
        ObjectResponse<String> response = new ObjectResponse<>();
        if(StringUtils.isBlank(parameter2)) {
            response.setData("删除成功");
        } else {
            response.setData("删除成功,parameter2=" + parameter2);
        }
        return response;
    }

    @GetMapping("test1")
    public ObjectResponse<MyVO> test1(@RequestParam("name") String name,
                                      @RequestParam(value = "parameter2", defaultValue = "") String parameter2) {
        MyVO myVO = new MyVO();
        if(StringUtils.isBlank(parameter2)) {
            myVO.setField1("你好，" + name);
        } else {
            myVO.setField1("你好，" + name + ",parameter2=" + parameter2);
        }
        myVO.setField2(true);

        ObjectResponse<MyVO> response = new ObjectResponse<>();
        response.setData(myVO);
        return response;
    }

    @PostMapping("testPost")
    public ObjectResponse<String> testPost(@RequestBody(required = false) List<MyPostVO> myPostVOList,
                                           @RequestParam(value = "parameter2", defaultValue = "") String parameter2) {
        if(myPostVOList==null || myPostVOList.size()<=0) {
            throw new IllegalArgumentException("没有指定myPostVOList");
        }

        log.debug("接收到客户端提交" + myPostVOList.size() + "个MyPostVO，如下: " + myPostVOList);
        ObjectResponse<String> response = new ObjectResponse<>();
        if(StringUtils.isBlank(parameter2)) {
            response.setData("调用成功");
        } else {
            response.setData("调用成功,parameter2=" + parameter2);
        }
        return response;
    }

    @PutMapping("testPut")
    public ObjectResponse<String> testPut(@RequestBody(required = false) List<MyPostVO> myPostVOList,
                                          @RequestParam(value = "parameter2", defaultValue = "") String parameter2) {
        if(myPostVOList==null || myPostVOList.size()<=0) {
            throw new IllegalArgumentException("没有指定myPostVOList");
        }

        log.debug("接收到客户端提交" + myPostVOList.size() + "个MyPostVO，如下: " + myPostVOList);
        ObjectResponse<String> response = new ObjectResponse<>();
        if(StringUtils.isBlank(parameter2)) {
            response.setData("调用成功");
        } else {
            response.setData("调用成功,parameter2=" + parameter2);
        }
        return response;
    }

    @GetMapping("testHeaderWithToken")
    public ObjectResponse<String> testHeaderWithToken(@RequestHeader("token") String token,
                                                      @RequestParam(value = "parameter2", defaultValue = "") String parameter2) {
        ObjectResponse<String> response = new ObjectResponse<>();
        if(StringUtils.isBlank(parameter2)) {
            response.setData("你提交的token=" + token);
        } else {
            response.setData("你提交的token=" + token + ",parameter2=" + parameter2);
        }
        return response;
    }

    @GetMapping("testHeaderWithDynamicToken")
    public ObjectResponse<String> testHeaderWithDynamicToken(@RequestHeader("dynamicToken") String dynamicToken,
                                                             @RequestParam(value = "parameter2", defaultValue = "") String parameter2) {
        ObjectResponse<String> response = new ObjectResponse<>();
        if(StringUtils.isBlank(parameter2)) {
            response.setData("你提交的dynamicToken=" + dynamicToken);
        } else {
            response.setData("你提交的dynamicToken=" + dynamicToken + ",parameter2=" + parameter2);
        }
        return response;
    }

    @GetMapping("testResponseWithHttpStatus400")
    public ResponseEntity<ObjectResponse<String>> testResponseWithHttpStatus400() {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(900);
        response.setErrorMessage("意料中异常,BAD_REQUEST: 400");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping(value = "upload")
    public ObjectResponse<String> upload(@RequestParam(value = "file", required = false) MultipartFile[] files,
                                         @RequestParam(value = "parameter2", defaultValue = "") String parameter2) throws IOException {
        if(files==null || files.length<=0) {
            throw new IllegalArgumentException("没有指定上传文件");
        }

        List<MultipartFile> multipartFiles = new ArrayList<>();
        for(MultipartFile fileTemporary : files) {
            if(!fileTemporary.isEmpty()) {
                multipartFiles.add(fileTemporary);
            }
        }

        log.debug("上传文件总数:{}", multipartFiles.size());

        File temporaryDirectory = Util.getTemporaryDirectory();
        for(MultipartFile fileTemporary : multipartFiles) {
            if(fileTemporary.isEmpty()) {
                continue;
            }
            String originalFilename = fileTemporary.getOriginalFilename();
            File file = new File(temporaryDirectory, Const.DemoDirectoryName + File.separator + Const.DirectoryUploaded + File.separator + originalFilename);

            // 创建所有父级目录
            file.mkdirs();

            fileTemporary.transferTo(file);

            String name = fileTemporary.getName();
            String contentType = fileTemporary.getContentType();
            long size = fileTemporary.getSize();

            log.debug("上传文件 提交表单名:{}, 文件名称:{}, 文件类型:{}, 大小:{}字节, 临时保存路径:{}"
                    , name, originalFilename, contentType, size, file.getAbsolutePath());
        }

        ObjectResponse<String> response = new ObjectResponse<>();
        if(StringUtils.isBlank(parameter2)) {
            response.setData("成功上传" + multipartFiles.size() + "个文件");
        } else {
            response.setData("成功上传" + multipartFiles.size() + "个文件,parameter2=" + parameter2);
        }
        return response;
    }

    @GetMapping(value="download/{filename}")
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
