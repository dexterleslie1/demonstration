package com.future.demo;

import com.future.common.http.ObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于协助调试jdwp
 */
@RestController
@RequestMapping(value = "/api/v1/jdwp")
@Slf4j
public class JdwpController {

    @GetMapping("debug")
    public ObjectResponse<String> debug(
            @RequestParam(value = "param1", defaultValue = "") String param1) throws Exception {

        if (param1.equals("exception")) {
            throw new Exception("测试jdwp条件断点");
        }

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("param1=" + param1);
        return response;
    }

}
