package com.future.demo.http;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value="/api/v1/")
public class GetMethodController {
    // 客户端使用url query param方式提交，服务端使用@RequestParam获取提交参数
    // curl -X GET http://localhost:8080/api/v1/testGetSubmitParamByUrl1?param1=v1
    @GetMapping(value="testGetSubmitParamByUrl1")
    public ResponseEntity<String> testGetSubmitParamByUrl1(
            @RequestParam(value = "param1",defaultValue = "") String param1){
        String responseString = "提交参数param1=" + param1;
        ResponseEntity<String> responseEntity=ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }

    // 使用url query param方式提交，服务端使用parameter binding方式获取提交参数
    // curl -X GET http://localhost:8080/api/v1/testGetSubmitParamByUrl2?param1=v1
    @GetMapping(value="testGetSubmitParamByUrl2")
    public ResponseEntity<String> testGetSubmitParamByUrl2(FormDataDTO dto){
        String responseString = "提交参数param1=" + dto.getParam1();
        ResponseEntity<String> responseEntity=ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }

    // 客户端使用application/json方式提交，服务端使用@RequestBody方式获取提交参数
    // curl -X GET -d '{"param1":"v1"}' -H "Content-Type: application/json" http://localhost:8080/api/v1/testGetSubmitParamByJSON
    @GetMapping(value="testGetSubmitParamByJSON")
    public ResponseEntity<String> testGetSubmitParamByJSON(@RequestBody FormDataDTO dto) {
        String responseString = "提交参数param1=" + dto.getParam1();
        ResponseEntity<String> responseEntity = ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }
}
