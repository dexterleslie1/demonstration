package com.future.demo.java;

//import com.yyd.common.http.response.ObjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
    @Autowired
    TestService testService;

//    @GetMapping("add")
//    ObjectResponse<Integer> add(@RequestParam("a") int a,
//                                @RequestParam("b") int b) {
//        int c = testService.add(a, b);
//        ObjectResponse<Integer> response = new ObjectResponse<>();
//        response.setData(c);
//        return response;
//    }
}
