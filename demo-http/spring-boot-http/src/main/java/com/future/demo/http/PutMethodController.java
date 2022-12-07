package com.future.demo.http;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value="/api/v1/")
public class PutMethodController {
    // 使用url query param方式提交，服务端使用@RequestParam方式获取提交参数
    // curl -X PUT http://localhost:8080/api/v1/testPutSubmitParamByUrl1?param1=v1
    @PutMapping(value="testPutSubmitParamByUrl1")
    public ResponseEntity<String> testPutSubmitParamByUrl1(
            @RequestParam(value = "param1",defaultValue = "") String param1){
        String responseString = "提交参数param1=" + param1;
        ResponseEntity<String> responseEntity=ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }

    // 客户端使用application/x-www-form-urlencoded方式提交，服务端使用@RequestParam方式获取提交参数
    // curl -X PUT -d "param1=v1" -H "Content-Type: application/x-www-form-urlencoded" http://localhost:8080/api/v1/testPutSubmitParamByFormUrlencoded1
    @PutMapping(value="testPutSubmitParamByFormUrlencoded1")
    public ResponseEntity<String> testPutSubmitParamByFormUrlencoded1(
            @RequestParam(value = "param1",defaultValue = "") String param1) {
        String responseString = "提交参数param1=" + param1;
        ResponseEntity<String> responseEntity = ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }

    // 客户端使用application/x-www-form-urlencoded方式提交，服务端使用parameter binding方式获取提交参数
    // curl -X PUT -d "param1=v1" -H "Content-Type: application/x-www-form-urlencoded" http://localhost:8080/api/v1/testPutSubmitParamByFormUrlencoded2
    @PutMapping(value="testPutSubmitParamByFormUrlencoded2")
    public ResponseEntity<String> testPutSubmitParamByFormUrlencoded2(FormDataDTO dto) {
        String responseString = "提交参数param1=" + dto.getParam1();
        ResponseEntity<String> responseEntity = ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }

    // 客户端使用application/json方式提交，服务端使用@RequestBody方式获取提交参数
    // curl -X PUT -d '{"param1":"v1"}' -H "Content-Type: application/json" http://localhost:8080/api/v1/testPutSubmitParamByJSON
    @PutMapping(value="testPutSubmitParamByJSON")
    public ResponseEntity<String> testPutSubmitParamByJSON(@RequestBody FormDataDTO dto) {
        String responseString = "提交参数param1=" + dto.getParam1();
        ResponseEntity<String> responseEntity = ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }
}
