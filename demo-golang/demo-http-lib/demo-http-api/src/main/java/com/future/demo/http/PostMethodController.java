package com.future.demo.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Slf4j
@Controller
@RequestMapping(value = "/api/v1/")
public class PostMethodController {
    // 使用url query param方式提交，服务端使用@RequestParam方式获取提交参数
    // curl -X POST http://localhost:8080/api/v1/testPostSubmitParamByUrl1?param1=v1
    @PostMapping(value = "testPostSubmitParamByUrl1")
    public ResponseEntity<String> testPostSubmitParamByUrl1(
            @RequestParam(value = "param1", defaultValue = "") String param1) {
        String responseString = "提交参数param1=" + param1;
        ResponseEntity<String> responseEntity = ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }

    // 客户端使用application/x-www-form-urlencoded方式提交，服务端使用@RequestParam方式获取提交参数
    // curl -X POST -d "param1=v1" -H "Content-Type: application/x-www-form-urlencoded" http://localhost:8080/api/v1/testPostSubmitParamByFormUrlencoded1
    @PostMapping(value = "testPostSubmitParamByFormUrlencoded1")
    public ResponseEntity<String> testPostSubmitParamByFormUrlencoded1(
            @RequestParam(value = "param1", defaultValue = "") String param1) {
        String responseString = "提交参数param1=" + param1;
        ResponseEntity<String> responseEntity = ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }

    // 客户端使用application/x-www-form-urlencoded方式提交，服务端使用parameter binding方式获取提交参数
    // curl -X POST -d "param1=v1" -H "Content-Type: application/x-www-form-urlencoded" http://localhost:8080/api/v1/testPostSubmitParamByFormUrlencoded2
    @PostMapping(value = "testPostSubmitParamByFormUrlencoded2")
    public ResponseEntity<String> testPostSubmitParamByFormUrlencoded2(FormDataDTO dto) {
        String responseString = "提交参数param1=" + dto.getParam1();
        ResponseEntity<String> responseEntity = ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }

    // 客户端使用multipart/form-data方式提交，服务端使用parameter binding方式获取提交参数
    // curl -X POST -F "param1=v1" -H "Content-Type: multipart/form-data" http://localhost:8080/api/v1/testPostSubmitParamByMultipartFormData
    @PostMapping(value = "testPostSubmitParamByMultipartFormData")
    public ResponseEntity<String> testPostSubmitParamByMultipartFormData(FormDataDTO dto) {
        String responseString = "提交参数param1=" + dto.getParam1();
        ResponseEntity<String> responseEntity = ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }

    // 客户端使用multipart/form-data方式提交，服务端使用@RequestParam方式获取提交参数
    // curl -X POST -F "param1=v1" -H "Content-Type: multipart/form-data" http://localhost:8080/api/v1/testPostSubmitParamByMultipartFormData2
    @PostMapping(value = "testPostSubmitParamByMultipartFormData2")
    public ResponseEntity<String> testPostSubmitParamByMultipartFormData2(
            @RequestParam(value = "param1", defaultValue = "") String param1) {
        String responseString = "提交参数param1=" + param1;
        ResponseEntity<String> responseEntity = ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }

    // 客户端使用application/json方式提交，服务端使用@RequestBody方式获取提交参数
    // curl -X POST -d '{"param1":"v1"}' -H "Content-Type: application/json" http://localhost:8080/api/v1/testPostSubmitParamByJSON
    @PostMapping(value = "testPostSubmitParamByJSON")
    public ResponseEntity<String> testPostSubmitParamByJSON(@RequestBody FormDataDTO dto) {
        String responseString = "提交参数param1=" + dto.getParam1();
        ResponseEntity<String> responseEntity = ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }

    // 测试响应返回字符串
    // curl -X POST http://localhost:8080/api/v1/testPostAndResponseWithString?name=dexter
    @PostMapping(value = "testPostAndResponseWithString")
    public ResponseEntity<Map<String, Object>> testPostAndResponseWithString(
            @RequestParam(value = "name", defaultValue = "") String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("请指定名字");
        }
        Map<String, Object> mapReturn = new HashMap<>();
        mapReturn.put("dataObject", "你好，" + name);
        ResponseEntity responseEntity = ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapReturn);
        return responseEntity;
    }

    // 测试响应JSON对象
    // curl -X POST http://localhost:8080/api/v1/testPostAndResponseWithJSONObject?name=dexter
    @PostMapping(value = "testPostAndResponseWithJSONObject")
    public ResponseEntity<Map<String, Object>> testPostAndResponseWithJSONObject(
            @RequestParam(value = "name", defaultValue = "") String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("请指定名字");
        }
        Map<String, Object> jsonObjectAsDataObject = new HashMap<>();
        jsonObjectAsDataObject.put("greeting", "你好，" + name);
        Map<String, Object> mapReturn = new HashMap<>();
        mapReturn.put("dataObject", jsonObjectAsDataObject);
        ResponseEntity responseEntity = ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapReturn);
        return responseEntity;
    }

    // 测试响应异常
    // curl -X POST http://localhost:8080/api/v1/testPostAndResponseWithException?name=dexter
    @PostMapping(value = "testPostAndResponseWithException")
    public ResponseEntity<Map<String, Object>> testPostAndResponseWithException(
            @RequestParam(value = "name", defaultValue = "") String name) throws Exception {
        boolean b = true;
        if (b) {
            throw new Exception("测试预期异常是否出现");
        }
        return null;
    }

    // 测试返回JSONArray
    // curl -X POST http://localhost:8080/api/v1/testPostAndResponseWithJSONArray?name=dexter
    @PostMapping(value = "testPostAndResponseWithJSONArray")
    public ResponseEntity<Map<String, Object>> testPostAndResponseWithJSONArray(
            @RequestParam(value = "name", defaultValue = "") String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("请指定名字");
        }
        List<String> listAsDataObject = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            listAsDataObject.add("你好，" + name + "#" + i);
        }
        Map<String, Object> mapReturn = new HashMap<>();
        mapReturn.put("dataObject", listAsDataObject);
        ResponseEntity responseEntity = ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapReturn);
        return responseEntity;
    }

    private final static String TemporaryDirectory = System.getProperty("java.io.tmpdir");

    /**
     * 上传
     *
     * @param name
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("upload")
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam("file1") MultipartFile file) throws IOException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("请指定名字");
        }

        String filename = file.getOriginalFilename();
        String fileAbsolute = TemporaryDirectory + File.separator + filename;
        File tempFile = new File(fileAbsolute);
        file.transferTo(tempFile);

        Map<String, Object> mapReturn = new HashMap<>();
        mapReturn.put("name", "你好，" + name);
        mapReturn.put("file", filename);

        ResponseEntity responseEntity = ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapReturn);
        return responseEntity;
    }

    /**
     * 下载
     *
     * @return
     * @throws Exception
     */
    @PostMapping(value = "download")
    public ResponseEntity<Map<String, Object>> download(
            @RequestParam(value = "filename", defaultValue = "") String filename) {
        if (StringUtils.isEmpty(filename)) {
            throw new IllegalArgumentException("请指定文件");
        }

        FileInputStream fileInputStream = null;
        ResponseEntity responseEntity = null;
        try {
            filename = TemporaryDirectory + File.separator + filename;
            fileInputStream = new FileInputStream(filename);
            responseEntity = ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"1.jpg\"")
                    .body(new InputStreamResource(fileInputStream));
        } catch (FileNotFoundException ex) {
            log.error(ex.getMessage(), ex);
            responseEntity = ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getMessage());
            return responseEntity;
        } finally {
        }
        return responseEntity;
    }
}
