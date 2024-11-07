# elasticsearch

## todo列表

> - 学会所有查询方法
> - SpringData elasticsearch学习
> - elasticsearch性能测试（高并发、大数据、集群、单机内存溢出）
> - 实现京东商品查询逻辑
> - elastcisearch多租户可行性分析

## es介绍

## es、lucene、solr区别

> es：基于lucene搜索引擎
>
> solr：基于lucene搜索引擎
>
> lucene：Lucene是非常优秀的成熟的 开源的 免费的纯 纯java 语言的全文索引检索工具包jar。 是搜索引擎的底层。
>
> todo

## es数据结构

> todo: 索引数据结构，集群索引如何分片和提供服务的。



## RESTful操作elasticsearch

### 使用 _analyze 查看分词情况

**指定analyzer查看分词**

```
GET /_analyze
{
    "analyzer": "ik_smart",
    "text": "我是程序员"
}
结果：
{
  "tokens" : [
    {
      "token" : "我",
      "start_offset" : 0,
      "end_offset" : 1,
      "type" : "CN_CHAR",
      "position" : 0
    },
    {
      "token" : "是",
      "start_offset" : 1,
      "end_offset" : 2,
      "type" : "CN_CHAR",
      "position" : 1
    },
    {
      "token" : "程序员",
      "start_offset" : 2,
      "end_offset" : 5,
      "type" : "CN_WORD",
      "position" : 2
    }
  ]
}

POST _analyze 
{
  "analyzer": "ik_max_word",
  "text": "千锋教育"
}
结果:
{
  "tokens" : [
    {
      "token" : "千",
      "start_offset" : 0,
      "end_offset" : 1,
      "type" : "TYPE_CNUM",
      "position" : 0
    },
    {
      "token" : "锋",
      "start_offset" : 1,
      "end_offset" : 2,
      "type" : "CN_CHAR",
      "position" : 1
    },
    {
      "token" : "教育",
      "start_offset" : 2,
      "end_offset" : 4,
      "type" : "CN_WORD",
      "position" : 2
    }
  ]
}
```

**使用指定索引的field查看分词**

```json
DELETE /test_analyze

PUT /test_analyze
{
  "mappings": {
    "properties": {
      "field1": {
        "type": "text",
        "analyzer": "ik_max_word"
      },
      "field2": {
        "type": "keyword"
      }
    }
  }
}

# 指定使用test_analyze索引的field1字段对"中华人民共和国"分词
POST /test_analyze/_analyze
{
  "field": "field1",
  "text": "中华人民共和国"
}

# 因为field2为keyword类型，所以下面不会分词
POST /test_analyze/_analyze
{
  "field": "field2",
  "text": "中华人民共和国"
}

```



### es数据类型

> 字符串类型：
>
> - text：一般用于全文检索，将当前field分词。
> - keyword：当前field不会被分词。
>
> 数值类型：
>
> - long
> - integer
> - short
> - byte
> - double
> - float
>
> 日期类型：
>
> - date：能够支持指定日期格式yyyy-MM-dd HH:mm:ss、yyyy-MM-dd、epoch_millis
>
> 布尔类型：
>
> - boolean：值只能够为true、false
>
> 二进制类型：
>
> - binary：支持存储基于Base64 encode后的字符串

### 索引操作

#### 创建索引

```text
# number_of_shards表示索引分片数为6
# number_of_replicas表示索引副本数为1
PUT /person 
{
  "settings": {
    "number_of_shards": 6,
    "number_of_replicas": 1
  }
}

结果:
{
  "acknowledged" : true,
  "shards_acknowledged" : true,
  "index" : "person"
}

PUT /blog
{
    "mappings": {
        "_doc": {
            "properties": {
                "id": {
                    "type": "long",
                    "store": true
                },
                "title": {
                    "type": "text",
                    "store": true,
                    "index": true,
                    "analyzer": "standard"
                },
                "content": {
                    "type": "text",
                    "store": true,
                    "index": true,
                    "analyzer": "standard"
                }
            }
        }
    }
}

结果:
{
    "acknowledged": true,
    "shards_acknowledged": true,
    "index": "blog"
}
```

#### 创建索引并指定settings和mappings

```json
PUT /book
{
  "settings": {
    "number_of_shards": 8,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "name": {
        # 类型是text，支持分词
        "type": "text",
        # 使用ik分词器的ik_max_word分词
        "analyzer": "ik_max_word"
      },
      "author": {
        # 作者字段不支持分词
        "type": "keyword"
      },
      "count": {
        "type": "long"
      },
      "on-sale": {
        "type": "date",
        # 日期类型支持以下3种之一
        "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
      },
      "descr": {
        "type": "text",
        "analyzer": "ik_max_word"
      }
    }
  }
}

结果：
{
  "acknowledged" : true,
  "shards_acknowledged" : true,
  "index" : "book"
}
```

#### 查看索引信息

```
GET /person

结果:
{
  "person" : {
    "aliases" : { },
    "mappings" : { },
    "settings" : {
      "index" : {
        "creation_date" : "1677335532848",
        "number_of_shards" : "6",
        "number_of_replicas" : "1",
        "uuid" : "nvUtgXiXR7aL1SJY_1wnRg",
        "version" : {
          "created" : "7080099"
        },
        "provided_name" : "person"
      }
    }
  }
}
```

#### 查看索引mappings

```
GET /book/_mapping
```

#### 为已存在的索引添加字段

> https://blog.csdn.net/m0_67393413/article/details/124242945

```
DELETE /test_mapping_update

PUT /test_mapping_update
{
  "mappings": {
    "properties": {
      "field1": {
        "type": "text"
      },
      "field2": {
        "type": "text"
      }
    }
  }
}

GET /test_mapping_update/_mapping

# 新增field3和field4字段
PUT /test_mapping_update/_mapping
{
  "properties": {
    "field3": {
      "type": "long"
    },
    "field4": {
      "type": "integer"
    }
  }
}

GET /test_mapping_update/_mapping
```



#### 删除索引

```
DELETE /person

结果:
{
  "acknowledged" : true
}
```

### 文档操作

#### 创建文档

> https://www.jianshu.com/p/3b4f1fe275d4

**自动生成id**

```text
POST /book/_doc
{
  "name": "盘龙",
  "author": "我吃西红柿",
  "count": 100000,
  "on-sale": "2000-01-01",
  "descr": "解放军诶uru日额反而就ierue积分"
}

结果：
{
  "_index" : "book",
  "_type" : "_doc",
  "_id" : "lvfajIYBGRCAoSjZfGcc",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 0,
  "_primary_term" : 1
}
```

**指定文档id**

```
POST /book/_doc/1
{
  "name": "红楼梦",
  "author": "曹雪芹",
  "count": 10000000,
  "on-sale": "1985-01-01",
  "descr": "据日u诶入耳哦u你科尔健康viueiruui额u解决ve佛恩道具ixww就iroe"
}

结果：
{
  "_index" : "book",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 1,
  "_primary_term" : 1
}
```

#### 修改文档

**覆盖式修改**

```text
PUT /book/_doc/1
{
  "name": "红楼梦1",
  "author": "曹雪芹1",
  "count": 11000000,
  "on-sale": "1986-01-01",
  "descr": "据日u诶入耳哦u你科尔健康viueiruui额u解决ve佛恩道具ixww就iroe"
}

结果：
{
  "_index" : "book",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 2,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 2,
  "_primary_term" : 1
}
```

**指定field修改**

```
POST /book/_update/1
{
  "doc": {
    "count": 123456
  }
}

结果：
{
  "_index" : "book",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 3,
  "result" : "noop",
  "_shards" : {
    "total" : 0,
    "successful" : 0,
    "failed" : 0
  },
  "_seq_no" : 3,
  "_primary_term" : 1
}
```

#### 删除文档

```text
DELETE /book/_doc/1

结果：
{
  "_index" : "book",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 8,
  "result" : "deleted",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 8,
  "_primary_term" : 1
}

# 根据id in(100,101)删除文档
POST /sms-logs/_delete_by_query
{
  "query": {
    "ids": {
      "values": [100,101]
    }
  }
}
```

#### 根据id查询文档

```text
GET /book/_doc/1

结果：
{
  "_index" : "book",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 1,
  "_seq_no" : 9,
  "_primary_term" : 1,
  "found" : true,
  "_source" : {
    "name" : "红楼梦",
    "author" : "曹雪芹",
    "count" : 10000000,
    "on-sale" : "1985-01-01",
    "descr" : "据日u诶入耳哦u你科尔健康viueiruui额u解决ve佛恩道具ixww就iroe"
  }
}
```

### es的查询

#### 准备测试数据

```json
DELETE /sms-logs

PUT /sms-logs
{
  "settings": {
    "number_of_shards": 5,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "province": {
        "type": "keyword"
      },
      "smsContent": {
        "type": "text"
        , "analyzer": "ik_max_word"
      },
      "corpName": {
        "type": "keyword"
      },
      "fee": {
        "type": "double"
      },
      "operatorId": {
        "type": "integer"
      }
    }
  }
}

POST /sms-logs/_doc/1
{
  "province": "广东",
  "smsContent": "我们一定都不陌生，无论中国城市还是乡间",
  "corpName": "中国移动",
  "fee": 11.2,
  "operatorId": 1
}
POST /sms-logs/_doc/2
{
  "province": "广东",
  "smsContent": "照亮了她们收货的脚下，也点亮了一安装个小小的家。",
  "corpName": "中国移动",
  "fee": 3.2,
  "operatorId": 2
}
POST /sms-logs/_doc/3
{
  "province": "湖南",
  "smsContent": "车前的灯照亮了北京这对母女送外卖的路，在次第亮起的感应灯护送下，她们穿梭在万家灯火之间。",
  "corpName": "盒马鲜生",
  "fee": 5.5,
  "operatorId": 2
}
POST /sms-logs/_doc/4
{
  "province": "北京",
  "smsContent": "这是三岁半的朦朦跟21岁的妈妈收货陈佳欣送外卖健康的第三年。",
  "corpName": "途虎养车",
  "fee": 6.8,
  "operatorId": 3
}
POST /sms-logs/_doc/5
{
  "province": "北京",
  "smsContent": "渐渐地，朦朦有了漂亮中国衣服、零食和货品玩具，她慢慢健康长大了，妈妈也可以在忙碌时暂时把她放下了。",
  "corpName": "途虎养车",
  "fee": 5,
  "operatorId": 3
}
POST /sms-logs/_doc/6
{
  "province": "湖南",
  "smsContent": "带着朦朦怕危险，放下心又悬着，妈妈也很矛盾。",
  "corpName": "中国平安股份有限公司",
  "fee": 20.23,
  "operatorId": 1
}
```

#### term和terms查询

> term查询是精确匹配，不会对搜索的关键字进行分词，直接使用关键字到分词库中检索是否有匹配的分词精确匹配关键字。
>
> terms是用于一个字段匹配多个值情景使用，例如: province=="北京" or province=="广东"。
>
> term和terms查询能够用于keyword、text类型字段，只是term和terms查询都不会对关键字分词。

**term查询**

```
# 查询province为北京的数据
POST /sms-logs/_search
{
  "from": 0,
  "size": 5,
  "query": {
    "term": {
      "province": {
        "value": "北京"
      }
    }
  }
}

结果：
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 2,
      "relation" : "eq"
    },
    "max_score" : 0.6931471,
    "hits" : [
      {
        "_index" : "sms-logs",
        "_type" : "_doc",
        "_id" : "5",
        "_score" : 0.6931471,
        "_source" : {
          "province" : "北京"
        }
      },
      {
        "_index" : "sms-logs",
        "_type" : "_doc",
        "_id" : "4",
        "_score" : 0.2876821,
        "_source" : {
          "province" : "北京"
        }
      }
    ]
  }
}
```

**terms查询**

```
# 查询province为北京或者广东的数据
POST /sms-logs/_search
{
  "query": {
    "terms": {
      "province": [
        "北京",
        "广东"
      ]
    }
  }
}

结果：
{
  "took" : 4,
  "timed_out" : false,
  "_shards" : {
    "total" : 5,
    "successful" : 5,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 4,
      "relation" : "eq"
    },
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "sms-logs",
        "_type" : "_doc",
        "_id" : "5",
        "_score" : 1.0,
        "_source" : {
          "province" : "北京"
        }
      },
      {
        "_index" : "sms-logs",
        "_type" : "_doc",
        "_id" : "4",
        "_score" : 1.0,
        "_source" : {
          "province" : "北京"
        }
      },
      {
        "_index" : "sms-logs",
        "_type" : "_doc",
        "_id" : "2",
        "_score" : 1.0,
        "_source" : {
          "province" : "广东"
        }
      },
      {
        "_index" : "sms-logs",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 1.0,
        "_source" : {
          "province" : "广东"
        }
      }
    ]
  }
}
```

#### match查询

##### match_all查询

```
# 查询所有数据
POST /sms-logs/_search
{
  "query": {
    "match_all": {}
  }
}
```

##### match查询

```
# 查询smsContent字段包含"收货"、"安装"分词的记录
POST /sms-logs/_search
{
  "query": {
    "match": {
      "smsContent": "收货安装"
    }
  }
}
```

##### 布尔match查询

```
# 查询smsContent同时包含 "中国"和"健康" 分词的记录
POST /sms-logs/_search
{
  "query": {
    "match": {
      "smsContent": {
        "query": "中国 健康",
        "operator": "and"
      }
    }
  }
}

# 查询smsContent包含 "中国" 或者 "健康" 分词的记录
POST /sms-logs/_search
{
  "query": {
    "match": {
      "smsContent": {
        "query": "中国 健康",
        "operator": "or"
      }
    }
  }
}
```

##### multi_match查询

> match是针对一个field做检索，multi_match是针对多个field进行检索，多个field对应一个text。

```
# 查询province、smsContent有北京分词的
POST /sms-logs/_search
{
  "query": {
    "multi_match": {
      "query": "北京",
      "fields": ["province", "smsContent"]
    }
  }
}
```

#### id和ids查询

```
# 查询id=1的sms-logs
GET /sms-logs/_doc/1

# 查询id in(1,2,3)，类似MySQL where id in查询
POST /sms-logs/_search
{
  "query": {
    "ids": {
      "values": [1, 2, 3]
    }
  }
}
```

#### prefix查询

> prefix前缀查询, 比如某个field是"途虎科技", 搜索词是"途虎";则可以查询出来; 和match的区别, 如果"途虎科技"是"keyword"类型, 是查询不到的。

```json
POST /sms-logs/_search
{
  "query": {
    "prefix": {
      "corpName": {
        "value": "途虎"
      }
    }
  }
}
```

#### fuzzy查询

> 支持有错别字查询

```
# 不能查询到任何结果，因为prefix_length=3表示前缀连续3个字符必须要匹配
POST /sms-logs/_search
{
  "query": {
    "fuzzy": {
      "corpName": {
        "value": "盒马先生",
        "prefix_length": 3
      }
    }
  }
}

# 能够查询到结果，因为prefix_length=2表示前缀只需要连续2个字符匹配即可
POST /sms-logs/_search
{
  "query": {
    "fuzzy": {
      "corpName": {
        "value": "盒马先生",
        "prefix_length": 2
      }
    }
  }
}
```

#### wildcard查询

> 通配符查询，和MySQL like是一致的，通过占位符 * 和 ? 表示通配符。

```
# 查询到以中国开头的记录
POST /sms-logs/_search
{
  "query": {
    "wildcard": {
      "corpName": {
        "value": "中国*"
      }
    }
  }
}

# 查询到以中国开头，后面有两个字的记录
POST /sms-logs/_search
{
  "query": {
    "wildcard": {
      "corpName": {
        "value": "中国??"
      }
    }
  }
}
```

#### range查询

```
# 查询fee在(5,10]之间的记录
POST /sms-logs/_search
{
  "query": {
    "range": {
      "fee": {
        "gt": 5,
        "lte": 10
      }
    }
  }
}
```

#### regexp查询

> 正则查询，通过自定义正则表达式查询。
>
> todo

#### 复合查询

##### bool查询

> 复合过滤器，将多个查询条件，以一定的逻辑组合在一起。
>
> - must： 所有的条件，用must组合在一起表示and的意思。
> - must_not：将must_not中的条件全部都不能匹配，表示not的意思。
> - should：所有条件，用should组合在一起表示or的意思。

```
# province是北京或者广东
# operatorId不能等于1
# smsContent必须包含中国、健康
POST /sms-logs/_search
{
  "query": {
    "bool": {
      "should": [
        {
          "term": {
            "province": {
              "value": "北京"
            }
          }
        },
        {
          "term": {
            "province": {
              "value": "广东"
            }
          }
        }
      ],
      "must_not": [
        {
          "term": {
            "operatorId": {
              "value": "1"
            }
          }
        }
      ],
      "must": [
        {
          "term": {
            "smsContent": {
              "value": "中国"
            }
          }
        },
        {
          "term": {
            "smsContent": {
              "value": "健康"
            }
          }
        }
      ]
    }
  }
}
```

##### boosting查询

> https://blog.csdn.net/co_zjw/article/details/109811491
>
> 返回匹配positive查询的文档，同时降低也匹配negative查询的文档的相关性得分。您可以使用boosting查询来降低某些文档的匹配度，而不必将它们从搜索结果中排除。
>
> - positive: 要运行的查询。返回的所有文档都必须与此查询匹配
> - negative: 查询用于降低匹配文档的相关性得分
> - negative_boost: 介于0～1.0之间的浮点数，用于降低与negative查询匹配的文档的相关性得分。

```
# 查询smsContent匹配"收货安装"分词，并且如果smsContent批评"小小"分词时分数打折扣为0.1
POST /sms-logs/_search
{
  "query": {
    "boosting": {
      "positive": {
        "match": {
          "smsContent": "收货安装"
        }
      },
      "negative": {
        "match": {
          "smsContent": "小小"
        }
      },
      "negative_boost": 0.1
    }
  }
}
```

#### filter查询

> https://www.cnblogs.com/qdhxhz/p/11493677.html
>
> query查询：这种语句在执行时既要计算文档是否匹配，还要计算文档相对于其他文档的匹配度有多高，匹配度越高，`_score` 分数就越高
>
> filter查询：过滤上下文中的语句在执行时**只关心文档是否和查询匹配，不会计算匹配度，也就是得分**。

```
# 查询结果没有计算相关性得分
POST /sms-logs/_search
{
  "query": {
    "bool": {
      "filter": [
        {
          "term": {
            "corpName": "中国移动"
          }
        },
        {
          "range": {
            "fee": {
              "gte": 10,
              "lte": 20
            }
          }
        }
      ]
    }
  }
}

# 进一步优化查询，因为是精准查询，不需要查询进行评分计算，只希望对文档进行包括或排除的计算，所以我们会使用 constant_score 查询以非评分模式来执行 term 查询并以一作为统一评分
POST /sms-logs/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "bool": {
          "must": [
            {
              "term": {
                "corpName": "中国移动"
              }
            },
            {
              "range": {
                "fee": {
                  "gte": 10,
                  "lte": 20
                }
              }
            }
          ]
        }
      }
    }
  }
}
```

#### 高亮查询

```
# 查询smsContent包含中国，并且高亮中国。
POST /sms-logs/_search
{
  "query": {
    "match": {
      "smsContent": "中国"
    }
  },
  "highlight": {
    "fields": {
      "smsContent": {}
    },
    "pre_tags": "<font color='red'>",
    "post_tags": "</font>"
  }
}
```

#### 聚合查询

##### 去除重复计数cardinality

```
# 去除重复的province统计总共有多少个province
POST /sms-logs/_search
{
  "aggs": {
    "provinceCount": {
      "cardinality": {
        "field": "province"
      }
    }
  }
}
```

### _bulk批量插入文档

```json
DELETE /test_bulk

PUT /test_bulk
{
  "mappings": {
    "properties": {
      "field1": {
        "type": "text",
        "analyzer": "ik_max_word"
      }
    }
  }
}

POST /test_bulk/_bulk
{"index": {"_id": "1"}}
{"field1": "架飞机偶然偶尔"}
{"index": {"_id": "2"}}
{"field1": "就iu热额"}

POST /test_bulk/_search
{
  "query": {
    "match_all": {}
  }
}
```

### querystring查询分析器查询文档

> 备注： querystring查询分析器查询文档和关键词查询文档不相同，querystring会自动使用查询分析器将查询语句分词后再根据关键词查询

```text
GET /blog/_doc/_search
{
    "query": {
        "query_string": {
            "default_field": "title",
            "query": "修改"
        }
    }
}

{
    "took": 3,
    "timed_out": false,
    "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 1,
            "relation": "eq"
        },
        "max_score": 0.5753642,
        "hits": [
            {
                "_index": "blog",
                "_type": "_doc",
                "_id": "1",
                "_score": 0.5753642,
                "_source": {
                    "id": 10001,
                    "title": "[修改]中文标题",
                    "content": "[修改]中文内容"
                }
            }
        ]
    }
}
```

## mapping字段里的参数fileds(innerfield)

> https://www.jb51.net/article/217207.htm

```
DELETE /test_innerfield

PUT /test_innerfield
{
  "mappings": {
    "properties": {
      "city": {
        "type": "text",
        "analyzer": "ik_max_word", 
        "fields": {
          "innerfield1": {
            "type": "keyword"
          },
          "innerfield2": {
            "type": "text",
            "analyzer": "english"
          }
        }
      }
    }
  }
}

POST /test_innerfield/_bulk
{"index": {}}
{"city": "广州市"}
{"index": {}}
{"city": "北京市"}
{"index": {}}
{"city": "上海市"}

# 因为city分词类型是text，没有分词匹配关键词“广”
POST /test_innerfield/_search
{
  "query": {
    "term": {
      "city": {
        "value": "广"
      }
    }
  }
}

# 因为city.innerfield2使用english分词器分词，即把各个中文一个汉字为一个分词，所以能够匹配关键词“广”
POST /test_innerfield/_search
{
  "query": {
    "term": {
      "city.innerfield2": {
        "value": "广"
      }
    }
  }
}

# 因为city.innerfield1是keyword类型不分词，所以能够匹配整个关键词“广州市”
POST /test_innerfield/_search
{
  "query": {
    "term": {
      "city.innerfield1": {
        "value": "广州市"
      }
    }
  }
}
```

## 多值字段

> https://www.manongdao.com/article-2053441.html

```
DELETE /test_beehive_han_v1

##新增一个索引的mapping
PUT test_beehive_han_v1 
{
  "settings": {

        "number_of_shards": "5",
        "number_of_replicas": "1"

  },
	"mappings": {
        "properties": {
          "contentTag": {
            "type": "text",
            "fields": {
              "raw": {
                "type": "keyword"
              }
            },
            "analyzer": "ik_smart"
          },
          "userTag": {
            "type": "text",
            "fields": {
              "raw": {
                "type": "keyword"
              }
            },
            "analyzer": "ik_smart"
          },
          "contentTagNotList": {
            "type": "text",
            "fields": {
              "keyword1": {
                "type": "keyword"
              }
            },
            "analyzer": "ik_smart"
          },
          "userTagNotList": {
            "type": "text",
            "fields": {
              "raw": {
                "type": "keyword"
              }
            },
            "analyzer": "ik_smart"
          }
        }
	}
}

##查看mapping
GET test_beehive_han_v1/_mapping

##插入数据：
POST test_beehive_han_v1/_doc/1
{
	"contentTagNotList":"广场舞2014,广场舞,广场舞大全,最新广场舞,广场舞教学",
	"userTagNotList":"外星人,罗纳尔多,梅西,C罗,皇马,巴萨,大罗,小罗,内马尔,卡卡",
	"contentTag": ["广场舞2014","广场舞","广场舞大全","最新广场舞","广场舞教学"],
	"userTag":["外星人","罗纳尔多","梅西","C罗","皇马","巴萨","大罗","小罗","内马尔","卡卡"]
}

POST test_beehive_han_v1/_doc/2
{
	"contentTagNotList":"广场舞2014,广场舞",
	"userTagNotList":"外星人,罗纳尔多,梅西",
	"contentTag": ["广场舞2014","广场舞"],
	"userTag":["外星人","罗纳尔多","只要包含"]
}

##查看数据
GET test_beehive_han_v1/_search

##查询-List的精确查询，比如：需要同时包含这几个值的都要被查询出来
GET test_beehive_han_v1/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "term": {
            "userTag.raw": {
              "value": "外星人"
            }
          }
        },{
          "term": {
            "userTag.raw": {
              "value": "罗纳尔多"
            }
          }
        },{
          "term": {
            "userTag.raw": {
              "value": "梅西"
            }
          }
        }
      ]
    }
  }
}

##非List的精确查询
GET test_beehive_han_v1/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "term": {
            "userTagNotList.raw": {
              "value": "外星人,罗纳尔多,梅西"
            }
          }
        }
      ]
    }
  }
}


##List的全文检索 //"query": "西"，梅西，罗纳尔多
GET test_beehive_han_v1/_search
{
  "profile": "true",
  "query": {
    "bool": {
      "must": [
        {
          "multi_match": {
            "query": "只要", 
            "fields": [
              "userTag"
            ]
          }
        }
      ]
    }
  }
}

## 非List的全文检索 "query": "罗纳尔多"
GET test_beehive_han_v1/_search
{
  "profile": "true",
  "query": {
    "bool": {
      "must": [
        {
          "multi_match": {
            "query": "皇马",
            "fields": [
              "userTagNotList"
            ]
          }
        }
      ]
    }
  }
}

##查看分词的情况
POST test_beehive_han_v1/_analyze
{
  "field": "userTag",
  "text" : "外星人,罗纳尔多,梅西"
}

POST /test_beehive_han_v1/_analyze
{
  "field": "userTag.raw",
  "text": "外星人,罗纳尔多,梅西"
}
```



## 使用Java Transport Client操作elasticsearch

> 具体用法参考代码<br>
[Java Transport Client](https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/index.html)

## Elasticsearch中什么是 tokenizer、analyzer、filter ?
[Elasticsearch中什么是 tokenizer、analyzer、filter ?](https://cloud.tencent.com/developer/article/1706529)

## 进阶编

### 使用term、filter搜索帖子数据

```
DELETE /article

PUT /article
{
  "mappings": {
    "properties": {
      "articleId": {
        "type": "keyword"
      },
      "userId": {
        "type": "long"
      },
      "hidden": {
        "type": "boolean"
      },
      "postDate": {
        "type": "date",
        "format": "yyyy-MM-dd||yyyy-MM-dd HH:mm:ss"
      }
    }
  }
}

POST /article/_bulk
{"index": {"_id": "1"}}
{"articleId": "001", "userId": 1, "hidden": false, "postDate": "2017-01-01"}
{"index": {"_id": "2"}}
{"articleId": "002", "userId": 1, "hidden": false, "postDate": "2017-01-02"}
{"index": {"_id": "3"}}
{"articleId": "003", "userId": 2, "hidden": false, "postDate": "2017-01-01"}
{"index": {"_id": "4"}}
{"articleId": "004", "userId": 2, "hidden": true, "postDate": "2017-01-02"}

# 根据用户id搜索帖子
POST /article/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "term": {
          "userId": 1
        }
      }
    }
  }
}

# 搜索没有隐藏的帖子
POST /article/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "term": {
          "hidden": false
        }
      }
    }
  }
}

# 搜索2017-01-02发出的贴子
POST /article/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "term": {
          "postDate": "2017-01-02"
        }
      }
    }
  }
}
```

### 基于bool组合多个filter条件搜素数据

```
# 搜索发帖日期为2017-01-01或者帖子id为003同时发帖日志不能为2017-01-02
POST /article/_search
{
  "query": {
    "bool": {
      "filter": [
        {
          "bool": {
            "should": [
            {
              "term": {
                "postDate": {
                  "value": "2017-01-01"
                }
              }
            },
            {
              "term": {
                "articleId": {
                  "value": "003"
                }
              }
            }
          ],
          "must_not": [
            {"term": {
              "postDate": {
                "value": "2017-01-02"
              }
            }}
          ]
          }
        }
      ]
    }
  }
}

# 搜索帖子id为003或者id为004并且发帖日期为2017-01-02
POST /article/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "bool": {
          "should": [
            {
              "term": {
                "articleId": {
                  "value": "003"
                }
              }
            },
            {
              "bool": {
                "must": [
                  {
                    "term": {
                      "articleId": {
                        "value": "004"
                      }
                    }
                  },
                  {
                    "term": {
                      "postDate": {
                        "value": "2017-01-02"
                      }
                    }
                  }
                ]
              }
            }
          ]
        }
      }
    }
  }
}
```

### terms搜索多个值以及多值搜索

```
# 新增tags字段
PUT /article/_mapping
{
  "properties": {
    "tags": {
      "type": "text"
    }
  }
}

GET /article/_mapping

# 批量修改记录tags字段
POST /article/_bulk
{"update": {"_id": "1"}}
{"doc": {"tags": ["java", "hadoop"]}}
{"update": {"_id": "2"}}
{"doc": {"tags": ["java"]}}
{"update": {"_id": "3"}}
{"doc": {"tags": ["hadoop"]}}
{"update": {"_id": "4"}}
{"doc": {"tags": ["java", "elasticsearch"]}}

GET /article/_search

# 搜索tags包含 ”java“ 关键词的帖子
POST /article/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "terms": {
          "tags": [
            "java"
          ]
        }
      }
    }
  }
}

# 使用terms查询articleId为002或者004
POST /article/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "terms": {
          "articleId": [
            "002",
            "004"
          ]
        }
      }
    }
  }
}
```

### range filter范围过滤

```
PUT /article/_mapping
{
  "properties": {
    "view_cnt": {
      "type": "long"
    }
  }
}

GET /article/_mapping

POST /article/_bulk
{"update": {"_id": "1"}}
{"doc": {"view_cnt":30}}
{"update": {"_id": "2"}}
{"doc": {"view_cnt": 50}}
{"update": {"_id": "3"}}
{"doc": {"view_cnt": 100}}
{"update": {"_id": "4"}}
{"doc": {"view_cnt": 80}}

# 搜索浏览量在30到60之间的贴子
POST /article/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "range": {
          "view_cnt": {
            "gt": 30,
            "lt": 60
          }
        }
      }
    }
  }
}
```

