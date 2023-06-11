package com.future.demo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

// todo 启动自动创建索引
@Document(indexName = "index_noteitem", type = "_doc", shards = 5, replicas = 1)
@Data
public class NoteItem {
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;

    @Field(type = FieldType.Long)
    private Long primaryId;
}
