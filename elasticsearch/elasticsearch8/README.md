# elasticsearch用法

## 注意

> 无法使用elasticsearch-rest-high-level-client操作elasticsearch8，可能是因为elasticsearch8 docker启动不正确导致

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
