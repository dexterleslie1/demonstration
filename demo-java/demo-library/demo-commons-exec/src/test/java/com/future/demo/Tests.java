package com.future.demo;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class Tests {
    @Test
    public void test() throws IOException {
        // region 基本用法

        String command = "ping localhost -c 5";
        //接收正常结果流
        ByteArrayOutputStream susStream = new ByteArrayOutputStream();
        //接收异常结果流
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        CommandLine commandLine = CommandLine.parse(command);
        DefaultExecutor exec = DefaultExecutor.builder().get();
        PumpStreamHandler streamHandler = new PumpStreamHandler(susStream, errStream);
        exec.setStreamHandler(streamHandler);
        int code = exec.execute(commandLine);
        System.out.println("退出代码: " + code);
        System.out.println(susStream.toString("GBK"));
        System.out.println(errStream.toString("GBK"));

        // endregion

        // region 执行命令时传入环境变量

        command = "printenv";
        //接收正常结果流
        susStream = new ByteArrayOutputStream();
        //接收异常结果流
        errStream = new ByteArrayOutputStream();
        commandLine = CommandLine.parse(command);
        exec = DefaultExecutor.builder().get();
        streamHandler = new PumpStreamHandler(susStream, errStream);
        exec.setStreamHandler(streamHandler);
        code = exec.execute(commandLine, new HashMap<String, String>() {{
            this.put("MY_ENV_VAR", "my_value");
        }});
        System.out.println("退出代码: " + code);
        System.out.println(susStream.toString("GBK"));
        System.out.println(errStream.toString("GBK"));

        // endregion
    }
}
