//package com.future.demo;
//
//import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
//import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
//import org.apache.jmeter.samplers.SampleResult;
//
///**
// * @author dexterleslie@gmail.com
// */
//public class LockLoadTest extends AbstractJavaSamplerClient {
//    private RedissonLockLoadTester tester = new RedissonLockLoadTester();
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
//            this.tester.doLock();
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
