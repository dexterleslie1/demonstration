package com.future.demo.performance;

import com.future.common.http.ObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/cpu")
@Slf4j
public class ApiCPUController {
    @Autowired
    CpuService cpuService;

    /**
     * 用于协助测试cpu负载
     *
     * @return
     */
    @RequestMapping("encode")
    public ObjectResponse<String> encode() {
        String str = this.cpuService.consume();

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData(str);
        return response;
    }

    @RequestMapping("startConsume")
    public ObjectResponse<String> startConsume() {
        this.cpuService.startConsume();

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("启动成功");
        return response;
    }

    @RequestMapping("stopConsume")
    public ObjectResponse<String> stopConsume() throws InterruptedException {
        this.cpuService.stopConsume();

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("停止成功");
        return response;
    }
}
