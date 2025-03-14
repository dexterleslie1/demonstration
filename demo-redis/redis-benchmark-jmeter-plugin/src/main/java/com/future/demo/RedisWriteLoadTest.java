//package com.future.demo;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
//import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
//import org.apache.jmeter.samplers.SampleResult;
//
///**
// * @author dexterleslie@gmail.com
// */
//public class RedisWriteLoadTest extends AbstractJavaSamplerClient {
//    private WriteLoadTester tester = new WriteLoadTester();
//
////    @Override
////    public Arguments getDefaultParameters() {
////        Date date = new Date();
//////        System.out.println(date + " - getDefaultParameters方法被调用");
////        Arguments arguments = new Arguments();
////        arguments.addArgument("var1","测试变量1");
////        return arguments;
////    }
//
//    @Override
//    public void setupTest(JavaSamplerContext context) {
//        this.tester.setup();
//    }
//
//    @Override
//    public SampleResult runTest(JavaSamplerContext context) {
//        SampleResult result = new SampleResult();
//        result.sampleStart();
//
//        if(this.tester != null){
//            try {
//                this.tester.doInsert();
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//        }
//
//        result.sampleEnd();
//        result.setSuccessful(true);
//        result.setDataType(SampleResult.TEXT);
//        return result;
//    }
//
//    @Override
//    public void teardownTest(JavaSamplerContext context) {
//        if(this.tester != null){
//            this.tester.teardown();
//        }
//    }
//}
