# elasticsearch用法

## todo列表

- 学会所有查询方法
- SpringData elasticsearch学习
- elasticsearch性能测试（高并发、大数据、集群、单机内存溢出）
- 实现京东商品查询逻辑
- elastcisearch多租户可行性分析

## elasticsearch java客户端分类

> [Java Transport Client](https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/index.html) <br>
[Java High Level REST Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html) <br>
[Java Low Level REST Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-low.html) <br><br>
*较新版本elasticsearch推荐使用这个客户端操作elasticsearch*<br>
[Elasticsearch Java API Client](https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/index.html)

## 使用docker-compose运行elasticsearch

```shell script
# 创建读写权限的 ~/data-demo-elk-elasticsearch 目录
sudo mkdir ~/data-demo-elk-elasticsearch
sudo chmod -R a+w ~/data-demo-elk-elasticsearch

# 启动elasticsearch
docker-compose up -d

# 删除elasticsearch
docker-compose down
```

## 使用postman操作elasticsearch

### 创建索引

```text
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

{
    "acknowledged": true,
    "shards_acknowledged": true,
    "index": "blog"
}
```

### 设置索引_mappings

```text
PUT /blog/_doc/_mappings
{
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

{
    "acknowledged": true
}
```

### 删除索引

```text
DELETE /blog

{
    "acknowledged": true
}
```

### 创建文档

```text
POST /blog/_doc/1
{
    "id": 10001,
    "title": "中文标题",
    "content": "中文内容"
}

{
    "_index": "blog",
    "_type": "_doc",
    "_id": "1",
    "_version": 1,
    "result": "created",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 0,
    "_primary_term": 1
}
```

### 删除文档

```text
DELETE /blog/_doc/1

{
    "_index": "blog",
    "_type": "_doc",
    "_id": "1",
    "_version": 2,
    "result": "deleted",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 2,
    "_primary_term": 1
}
```

### 修改文档

```text
POST /blog/_doc/1
{
    "id": 10001,
    "title": "[修改]中文标题",
    "content": "[修改]中文内容"
}

{
    "_index": "blog",
    "_type": "_doc",
    "_id": "1",
    "_version": 1,
    "result": "created",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 4,
    "_primary_term": 1
}
```

### 根据id查询文档

```text
GET /blog/_doc/1

{
    "_index": "blog",
    "_type": "_doc",
    "_id": "1",
    "_version": 1,
    "_seq_no": 4,
    "_primary_term": 1,
    "found": true,
    "_source": {
        "id": 10001,
        "title": "[修改]中文标题",
        "content": "[修改]中文内容"
    }
}
```

### 关键词查询文档

```text
GET /blog/_doc/_search
{
    "query": {
        "term": {
            "title": "文"
        }
    }
}

{
    "took": 28,
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
        "max_score": 0.2876821,
        "hits": [
            {
                "_index": "blog",
                "_type": "_doc",
                "_id": "1",
                "_score": 0.2876821,
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

### 查看分词器的分词效果

```text
GET /_analyze
{
    "analyzer": "standard",
    "text": "中文内容1"
}

{
    "tokens": [
        {
            "token": "中",
            "start_offset": 0,
            "end_offset": 1,
            "type": "<IDEOGRAPHIC>",
            "position": 0
        },
        {
            "token": "文",
            "start_offset": 1,
            "end_offset": 2,
            "type": "<IDEOGRAPHIC>",
            "position": 1
        },
        {
            "token": "内",
            "start_offset": 2,
            "end_offset": 3,
            "type": "<IDEOGRAPHIC>",
            "position": 2
        },
        {
            "token": "容",
            "start_offset": 3,
            "end_offset": 4,
            "type": "<IDEOGRAPHIC>",
            "position": 3
        },
        {
            "token": "1",
            "start_offset": 4,
            "end_offset": 5,
            "type": "<NUM>",
            "position": 4
        }
    ]
}
```

### 查看中文分词器ik的分词效果

```text
GET /_analyze
{
    "analyzer": "ik_smart",
    "text": "我是程序员"
}

{
    "tokens": [
        {
            "token": "我",
            "start_offset": 0,
            "end_offset": 1,
            "type": "CN_CHAR",
            "position": 0
        },
        {
            "token": "是",
            "start_offset": 1,
            "end_offset": 2,
            "type": "CN_CHAR",
            "position": 1
        },
        {
            "token": "程序员",
            "start_offset": 2,
            "end_offset": 5,
            "type": "CN_WORD",
            "position": 2
        }
    ]
}

GET /_analyze
{
    "analyzer": "ik_max_word",
    "text": "我是程序员"
}

{
    "tokens": [
        {
            "token": "我",
            "start_offset": 0,
            "end_offset": 1,
            "type": "CN_CHAR",
            "position": 0
        },
        {
            "token": "是",
            "start_offset": 1,
            "end_offset": 2,
            "type": "CN_CHAR",
            "position": 1
        },
        {
            "token": "程序员",
            "start_offset": 2,
            "end_offset": 5,
            "type": "CN_WORD",
            "position": 2
        },
        {
            "token": "程序",
            "start_offset": 2,
            "end_offset": 4,
            "type": "CN_WORD",
            "position": 3
        },
        {
            "token": "员",
            "start_offset": 4,
            "end_offset": 5,
            "type": "CN_CHAR",
            "position": 4
        }
    ]
}
```

## 使用Java Transport Client操作elasticsearch

> 具体用法参考代码<br>
[Java Transport Client](https://www.elastic.co/guide/en/elasticsearch/client/java-api/6.8/index.html)

## Elasticsearch中什么是 tokenizer、analyzer、filter ?
[Elasticsearch中什么是 tokenizer、analyzer、filter ?](https://cloud.tencent.com/developer/article/1706529)

## 问题列表

### 访问 kibana 时，提示 "Data too large, data for [<http_request>] would be ..." 错误

错误信息如下：

```json
{"statusCode":500,"error":"Internal Server Error","message":"[parent] Data too large, data for [<http_request>] would be [514390462/490.5mb], which is larger than the limit of [510027366/486.3mb], real usage: [514389880/490.5mb], new bytes reserved: [582/582b], usages [request=0/0b, fielddata=0/0b, in_flight_requests=582/582b, model_inference=0/0b, eql_sequence=0/0b, accounting=10639028/10.1mb]: [circuit_breaking_exception] [parent] Data too large, data for [<http_request>] would be [514390462/490.5mb], which is larger than the limit of [510027366/486.3mb], real usage: [514389880/490.5mb], new bytes reserved: [582/582b], usages [request=0/0b, fielddata=0/0b, in_flight_requests=582/582b, model_inference=0/0b, eql_sequence=0/0b, accounting=10639028/10.1mb], with { bytes_wanted=514390462 & bytes_limit=510027366 & durability=\"PERMANENT\" }"}
```

经过分析，是 GC 回收后都无法释放 elasticsearch jvm内存导致，解决办法：调整 elasticsearch 内存为 Xmx1024m 或者更高值