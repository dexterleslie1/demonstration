package com.future.demo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "index_noteitem", shards = 5, replicas = 1)
// https://stackoverflow.com/questions/63810021/create-custom-analyzer-with-asciifolding-filter-in-spring-data-elasticsearch
// 自定义analyzer
@Setting(settingPath = "/my-analyzer.json")
@Data
public class NoteItem {
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word_pinyin")
    private String content;

    @Field(type = FieldType.Long)
    private Long primaryId;
}
