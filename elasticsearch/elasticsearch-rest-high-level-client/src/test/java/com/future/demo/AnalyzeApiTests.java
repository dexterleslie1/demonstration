package com.future.demo;

/**
 * TODO 升级elasticsearch-rest-high-level-client都7.8.0后analyze api不兼容
 */
public class AnalyzeApiTests extends AbstractTestSupport{
//    /**
//     *
//     */
//    @Test
//    public void analyzeSynchronous() throws IOException {
//        AnalyzeRequest request = new AnalyzeRequest();
//        request.text("Some text to analyze", "Some more text to analyze");
//        request.analyzer("standard");
//        AnalyzeResponse response = client.indices().analyze(request, RequestOptions.DEFAULT);
//        List<AnalyzeResponse.AnalyzeToken> analyzeTokenList = response.getTokens();
//        Assert.assertTrue(analyzeTokenList.size()>0);
//    }
//
//    /**
//     *
//     * @throws InterruptedException
//     * @throws TimeoutException
//     */
//    @Test
//    public void analyzeAsynchronous() throws InterruptedException, TimeoutException {
//        AnalyzeRequest request = new AnalyzeRequest();
//        request.text("Some text to analyze", "Some more text to analyze");
//        request.analyzer("standard");
//        final CountDownLatch countDownLatch1 = new CountDownLatch(1);
//        final AtomicInteger atomicInteger = new AtomicInteger();
//        client.indices().analyzeAsync(request, RequestOptions.DEFAULT, new ActionListener<AnalyzeResponse>() {
//            public void onResponse(AnalyzeResponse analyzeTokens) {
//                countDownLatch1.countDown();
//            }
//
//            public void onFailure(Exception e) {
//                atomicInteger.incrementAndGet();
//            }
//        });
//
//        if(!countDownLatch1.await(1, TimeUnit.SECONDS)) {
//            throw new TimeoutException();
//        }
//        Assert.assertEquals(0, atomicInteger.get());
//    }
}
