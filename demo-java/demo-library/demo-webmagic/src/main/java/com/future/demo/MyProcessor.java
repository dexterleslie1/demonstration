package com.future.demo;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.HtmlNode;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

public class MyProcessor implements PageProcessor {
    // 爬虫站点的一些配置，如编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public void process(Page page) {
        // 使用CSS选择器抽取文章标题和链接
        HtmlNode htmlNode = (HtmlNode) page.getHtml().css("title");
        String title = htmlNode.xpath("/title/text()").toString();
        page.putField("title", title);
        page.putField("link", page.getUrl().toString());

//        // 如果页面中有更多文章链接，可以添加它们到待抓取队列
//        page.addTargetRequests(page.getHtml().css("a.next-page").links().all());
        List<Selectable> aList = page.getHtml().xpath("//a[starts-with(@href,'http://')]").nodes();
//        page.addTargetRequest(aList.get(0));
//        HtmlNode htmlNode1 = (HtmlNode) aList.get(0);
//        String href = htmlNode1.xpath("//a/@href").toString();
//        page.addTargetRequest(href);
    }

    @Override
    public Site getSite() {
        return site;
    }
}
