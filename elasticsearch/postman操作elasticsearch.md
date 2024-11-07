# `postman`操作`elasticsearch`



## 创建索引

```json
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



## 设置索引`_mappings`

```json
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



## 删除索引

```json
DELETE /blog

{
    "acknowledged": true
}
```



## 创建文档

```json
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



## 删除文档

```json
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



## 修改文档

```json
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



## 根据`id`查询文档

```json
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



## 关键词查询文档

```json
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



### `querystring`查询分析器查询文档

> 备注： `querystring`查询分析器查询文档和关键词查询文档不相同，`querystring`会自动使用查询分析器将查询语句分词后再根据关键词查询

```json
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



## 查看分词器的分词效果

```json
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

### 查看中文分词器`ik`的分词效果

```json
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

## 