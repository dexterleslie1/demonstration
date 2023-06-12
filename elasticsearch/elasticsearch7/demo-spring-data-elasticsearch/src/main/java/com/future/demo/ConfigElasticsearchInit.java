package com.future.demo;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class ConfigElasticsearchInit {

    @Resource
    ElasticsearchTemplate elasticsearchTemplate;

    @PostConstruct
    public void init() {
        /*------------------------------- 判断索引是否存在，是则删除 */
        // 判断索引是否存在
        boolean exists = this.elasticsearchTemplate.indexExists(NoteItem.class);
        // 删除索引
        boolean b = this.elasticsearchTemplate.deleteIndex(NoteItem.class);
        if (exists) {
            Assert.isTrue(b);
        } else {
            Assert.isTrue(!b);
        }

        /*------------------------------- 创建索引并修改索引的mappings */
        // 创建索引，但是没有mappings
        b = this.elasticsearchTemplate.createIndex(NoteItem.class);
        Assert.isTrue(b);
        // 更新索引mappings
        b = this.elasticsearchTemplate.putMapping(NoteItem.class);
        Assert.isTrue(b);
    }

}
