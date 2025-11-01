package com.future.demo;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/api/v1/")
public class ApiController {

    /**
     * 协助测试Http Client中文乱码问题
     *
     * @param response
     */
    @PostMapping(value = "test1")
    public void test1(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ObjectResponse<String> objectResponse = ResponseUtils.failObject(90001, "测试");
        String JSON = JSONUtil.toJsonStr(objectResponse);
        ServletUtil.write(response, JSON, MediaType.APPLICATION_JSON_UTF8_VALUE);
        // HTTP头Content-Type不响应MediaType.APPLICATION_JSON_UTF8_VALUE会导致中文乱码
        // ServletUtil.write(response, JSON, MediaType.APPLICATION_JSON_VALUE);
    }
}
