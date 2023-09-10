package com.future.demo;

import com.yyd.common.http.ResponseUtils;
import com.yyd.common.http.response.ObjectResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private Random random = new Random();

    @GetMapping("test1")
    ObjectResponse<String> test1() throws InterruptedException {
//        int randomInt = random.nextInt(300);
//        if(randomInt>0)
//            TimeUnit.MILLISECONDS.sleep(randomInt);

        String uuid = UUID.randomUUID().toString();
        return ResponseUtils.successObject(uuid);
    }

}
