package com.future.demo;

import com.future.common.exception.EnableFutureExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFutureExceptionHandler
public class Application {
    public static void main(String[] args) throws InterruptedException {
        String arg;
        if (args == null || args.length < 1) {
            // throw new IllegalArgumentException("请指定测试场景参数，例如：java -jar target/demo.jar xss");
            arg = "";
        } else {
            arg = args[0];
        }

        AssistantService service = new AssistantService();
        ArthasService arthasService = new ArthasService();
        if (arg.equals("xss"))
            service.investigateXss();
        else if (arg.equals("memallocpeak"))
            service.investigateMemoryAllocationPeak();
        else if (arg.equals("memalloc"))
            service.investigateMemoryAllocation();
        else if (arg.equals("arthas-trace"))
            arthasService.traceMethodLv1();
        else {
            SpringApplication.run(Application.class, args);
        }
    }
}
