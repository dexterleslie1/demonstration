//package com.future.demo;
//
//
//public class Applicationbak {
//    public static void main(String args[]) throws InterruptedException {
//        if (args == null || args.length < 1) {
//            throw new IllegalArgumentException("请指定测试场景参数，例如：java -jar target/demo.jar xss");
//        }
//
//        String arg = args[0];
//
//        AssistantService service = new AssistantService();
//        ArthasService arthasService = new ArthasService();
//        if (arg.equals("xss"))
//            service.investigateXss();
//        else if (arg.equals("memallocpeak"))
//            service.investigateMemoryAllocationPeak();
//        else if (arg.equals("memalloc"))
//            service.investigateMemoryAllocation();
//        else if (arg.equals("arthas-trace"))
//            arthasService.traceMethodLv1();
//        else {
//            throw new IllegalArgumentException("不支持测试场景参数 " + arg);
//        }
//    }
//}
