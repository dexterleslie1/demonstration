package com.future.demo.app;

import com.future.demo.pkg1.TestService1;
import com.future.demo.pkg1.TestService2;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Resource
    TestService1 testService1;
    @Resource
    TestService2 testService2;

    @GetMapping("add")
    ObjectResponse<Integer> add(@RequestParam("a") int a,
                                @RequestParam("b") int b) {
        int c = testService1.add(a, b);
        ObjectResponse<Integer> response = new ObjectResponse<>();
        response.setData(c);
        return response;
    }

    @GetMapping("minus")
    ObjectResponse<Integer> minus(@RequestParam("a") int a,
                                  @RequestParam("b") int b) {
        int c = testService2.minus(a, b);
        ObjectResponse<Integer> response = new ObjectResponse<>();
        response.setData(c);
        return response;
    }
}
