package com.future.demo;


public class Application {
    public static void main(String args[]) throws InterruptedException {
        if (args == null || args.length < 1) {
            throw new IllegalArgumentException("请指定测试场景参数，例如：java -jar target/demo.jar xss");
        }

        String arg = args[0];

        AssistantService service = new AssistantService();
        if (arg.equals("xss"))
            service.investigateXss();
        else {
            throw new IllegalArgumentException("不支持测试场景参数 " + arg);
        }
    }
}
