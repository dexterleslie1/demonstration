package com.future.demo.http;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value="/api/v1/")
public class DeleteMethodController {
    // 使用url query param方式提交，服务端使用@RequestParam方式获取提交参数
    // curl -X DELETE http://localhost:8080/api/v1/testDeleteSubmitParamByUrl1?param1=v1
    @DeleteMapping(value="testDeleteSubmitParamByUrl1")
    public ResponseEntity<String> testDeleteSubmitParamByUrl1(
            @RequestParam(value = "param1",defaultValue = "") String param1){
        String responseString = "提交参数param1=" + param1;
        ResponseEntity<String> responseEntity=ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }

    // 使用url query param方式提交，服务端使用parameter binding方式获取提交参数
    // curl -X DELETE http://localhost:8080/api/v1/testDeleteSubmitParamByUrl2?param1=v1
    @DeleteMapping(value="testDeleteSubmitParamByUrl2")
    public ResponseEntity<String> testDeleteSubmitParamByUrl2(FormDataDTO dto){
        String responseString = "提交参数param1=" + dto.getParam1();
        ResponseEntity<String> responseEntity=ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }

    // 客户端使用application/json方式提交，服务端使用@RequestBody方式获取提交参数
    // curl -X DELETE -d '{"param1":"v1"}' -H "Content-Type: application/json" http://localhost:8080/api/v1/testDeleteSubmitParamByJSON
    @DeleteMapping(value="testDeleteSubmitParamByJSON")
    public ResponseEntity<String> testDeleteSubmitParamByJSON(@RequestBody FormDataDTO dto) {
        String responseString = "提交参数param1=" + dto.getParam1();
        ResponseEntity<String> responseEntity = ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseString);
        return responseEntity;
    }
}
