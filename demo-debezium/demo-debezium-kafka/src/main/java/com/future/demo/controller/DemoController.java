package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.mapper.CommonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RequestMapping("/api/v1")
@RestController
@Slf4j
public class DemoController {

    @Resource
    CommonMapper commonMapper;

    /**
     * 批量插入 t_user 表
     *
     * @return
     */
    @GetMapping("testAssistInsertUserBatch")
    public ObjectResponse<String> testAssistInsertUserBatch() {
        List<Map<String, String>> userList = new ArrayList<>();
        for (int i = 0; i < 1024; i++) {
            userList.add(new HashMap<>() {{
                String uuidStr = UUID.randomUUID().toString();
                put("username", uuidStr);
                put("password", uuidStr);
            }});
        }
        int affectRows = commonMapper.insertUserBatch(userList);
        Assert.isTrue(affectRows == 1024);
        return ResponseUtils.successObject("成功调用");
    }
}
