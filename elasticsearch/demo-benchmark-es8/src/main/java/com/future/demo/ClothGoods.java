package com.future.demo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "cloth_goods")
@Setting(settingPath = "/my-analyzer.json")
@Data
public class ClothGoods {
    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long goodsId;

    // 企业Id
    @Field(type = FieldType.Long)
    private Long companyId;

    // cp(成品),pb(坯布)
    @Field(type = FieldType.Keyword)
    private String type;

    // 产品名称
    @Field(type = FieldType.Keyword)
    private String name;

    // 产品名称（wildcard类型，用于通配符查询性能测试）
    @Field(type = FieldType.Wildcard)
    private String nameWildcard;

    // 编号非空不超50位
    @Field(type = FieldType.Keyword)
    private String number;
}
