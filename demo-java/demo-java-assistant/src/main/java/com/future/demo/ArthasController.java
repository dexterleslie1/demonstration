package com.future.demo;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.SimpleTimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/api/v1/arthas")
@Slf4j
public class ArthasController {
    @Autowired
    ArthasService arthasService = null;

    @GetMapping("monitor")
    public ObjectResponse<String> monitorStart(
            @RequestParam(value = "loopCount", defaultValue = "100") int loopCount,
            @RequestParam(value = "sleepInterval", defaultValue = "100") int sleepInterval) throws InterruptedException {
        int count = 0;
        do {
            this.monitorMethod(sleepInterval);
            count++;
        } while (loopCount > 0 && count < loopCount);

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("调用成功");
        return response;
    }

    private void monitorMethod(int sleepInterval) throws InterruptedException {
        if (sleepInterval > 0) {
            Thread.sleep(sleepInterval);
        }
    }

    @GetMapping("watch")
    public ObjectResponse<String> watch() throws Exception {
        String uuid = UUID.randomUUID().toString();
        int intP = new Random().nextInt(3);
        this.arthasService.watchMethod(uuid, intP);

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("调用成功");
        return response;
    }

    @GetMapping("trace")
    public ObjectResponse<String> trace() throws InterruptedException {
        this.arthasService.traceMethodLv1();

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("调用成功");
        return response;
    }

    /**
     * 用于协助测试 monitor 内部类
     *
     * @return
     */
    @GetMapping("testInnerClass")
    public ObjectResponse<String> testInnerClass() {
        R r = new R();
        r.run();
        return ResponseUtils.successObject("成功调用");
    }

    private class R implements Runnable {

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * 用于协助测试 monitor、trace lambda表达式
     *
     * @return
     */
    @GetMapping("testLambdaExpression")
    public ObjectResponse<String> testLambdaExpression() {
        Runnable r = () -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
        };
        r.run();
        return ResponseUtils.successObject("成功调用");
    }
}
