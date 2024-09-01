package com.future.demo;

import org.junit.Test;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

import java.io.IOException;
import java.util.Collections;

public class Tests {
    @Test
    public void test() throws IOException {
        // 创建一个Spider，设置爬虫抓取入口和线程数，然后启动爬虫
        Spider.create(new MyProcessor())
                .addUrl("http://www.baidu.com") // 种子URL
                .thread(5)
                .setPipelines(Collections.singletonList(new ConsolePipeline()))
                .run();
    }
}
