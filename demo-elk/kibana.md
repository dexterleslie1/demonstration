# kibana



## KQL

Kibana中的KQL（Kibana Query Language）是一种使用自由文本搜索或基于字段的搜索来过滤Elasticsearch数据的简单语法。以下是对Kibana KQL的详细介绍：

**一、KQL的基本特点**

- **简化查询**：Kibana设计KQL是为了简化Elasticsearch的查询过程，提供一种更直观、易用的查询语法。
- **字段和语法补全**：KQL支持索引字段和语法补全功能，可以方便地查询数据。
- **逻辑运算符**：KQL支持逻辑运算符如and、or和not，允许用户组合多个查询条件。

**二、KQL的查询语法**

1. **等值匹配**：用于查询字段值是否等于指定值。
   - 语法：字段名:匹配值
   - 示例：`response:200`，匹配response字段中包含200的文档。
2. **关系运算符**：用于数值和时间类型字段的比较。
   - 支持的运算符：<=、>=、<、>
   - 示例：`age >= 30`，匹配年龄大于等于30的文档。
3. **逻辑运算符**：用于组合多个查询条件。
   - and：与操作，要求同时满足多个条件。
     - 示例：`name:jane and addr:beijing`，匹配name字段包含jane且addr字段包含beijing的记录。
   - or：或操作，满足其中一个条件即可。
     - 示例：`response:200 or response:404`，匹配response字段包含200或404的文档。
   - not：非操作，排除满足指定条件的文档。
     - 示例：`not response:200`，匹配response字段中不包含200的文档。
4. **通配符查询**：使用*匹配任意字符。
   - 示例：`machine.os:win*`，匹配machine.os字段以win开头的文档，如“windows 7”和“windows 10”。
5. **字段嵌套查询**：用于查询嵌套字段的值。
   - 示例：`level1.level2 { prop1: "foo" or prop1: "baz" }`，查找level1.level2.prop1是foo或baz的文档。
6. **日期范围查询**：使用@timestamp字段进行日期范围过滤。
   - 示例：`@timestamp < "2024-01-01"`，匹配日期在2024年1月1日之前的文档。
7. **Exists查询**：匹配包含指定字段的文档。
   - 示例：`exists:timestamp`，匹配具有timestamp字段的文档。

**三、KQL的使用场景**

- **数据过滤**：在Kibana的Discover、Visualize和Dashboard等模块中，使用KQL对数据进行过滤，以便进行更详细的分析和可视化。
- **实时监控**：结合Kibana的实时监控功能，使用KQL查询特定的数据，实现对系统或应用的实时监控。
- **数据探索**：在数据探索阶段，使用KQL快速定位和分析数据，以便更好地理解数据的特征和规律。

**四、注意事项**

- **大小写不敏感**：KQL查询不区分大小写。
- **优先级**：在KQL中，and的优先级高于or。如果需要改变优先级，可以使用括号进行分组。
- **性能考虑**：在使用通配符查询时，特别是前导通配符（如*abc），可能会影响查询性能。因此，在使用时应尽量避免或限制通配符的使用范围。

总的来说，Kibana的KQL提供了一种简单而强大的方式来查询和过滤Elasticsearch中的数据。通过熟练掌握KQL的基本语法和操作技巧，用户可以更高效地在Kibana中进行数据分析和可视化工作。



### 查询测试



#### 准备测试数据

```json
DELETE /test1

PUT /test1
{
    "mappings": {
        "properties": {
            "response": {
              "type": "integer",
              "store": true
            },
            "age": {
              "type": "integer",
              "store": true
            },
            "name": {
              "type": "text",
              "store": true,
              "index": true,
              "analyzer": "standard"
            },
            "createTime": {
              "type": "date",
              "store": true,
              "format": "yyyy-MM-dd HH:mm:ss"
            },
            "exists-test": {
              "type": "text",
              "store": true
            }
        }
    }
}

POST /test1/_doc/1
{
    "response": 200,
    "age": 35,
    "name": "zhangsan",
    "createTime": "2022-05-21 19:00:21"
}
POST /test1/_doc/2
{
    "response": 200,
    "age": 22,
    "name": "zhangsan-1",
    "createTime": "2022-05-12 22:00:21",
    "exists-test": "hello"
}
POST /test1/_doc/3
{
    "response": 401,
    "age": 21,
    "name": "apple pie",
    "createTime": "2022-03-22 01:23:46"
}
POST /test1/_doc/4
{
    "response": 500,
    "age": 101,
    "name": "apple ppp",
    "createTime": "2022-02-10 08:33:56"
}

```



#### 等值匹配

匹配 response 字段中包含 401 的文档

```
response:401
```



#### 关系运算符

匹配年龄大于 22 的文档

```
age>22
```



#### 逻辑运算符

**and**

匹配 response 字段包含 200 和 age 字段为 22 的文档

```
response:200 and age:22
```



**or**

匹配 response 字段包含 200 或 401 的文档

```
response:200 or response:401
```



**not**

匹配 response 字段中不包含 200 的文档

```
not response:200
```



#### 通配符查询

匹配 name 字段以 zhang 开头的文档，如“zhangsan”和“zhangsan-1”

```
name:zhang*
```



#### 日期范围查询

匹配日期 2022-03-03 00:00:00 和 2022-03-23 23:59:59 之间的文档

```
createTime>="2022-03-03 00:00:00" and createTime<="2022-03-23 23:59:59"
```



#### Exists 查询

匹配存在 exists-test 字段的文档

```
exists-test:*
```



#### 有双引号和没有双引号区别

在KQL（Kibana Query Language）中，使用双引号和没有使用双引号的主要区别在于搜索的精确性和匹配的范围。

**使用双引号**

当在KQL查询中使用双引号时，搜索引擎会将其中的内容视为一个完整的短语进行匹配。这意味着只有完全包含双引号内所有词语和词语顺序的文档才会被检索出来。例如，如果要查找包含“apple pie”这个短语的文档，可以在搜索框中输入：“apple pie”。这样，搜索引擎就会返回所有包含这个完整短语的文档，而不会返回只包含“apple”或“pie”的文档。

**没有使用双引号**

当在KQL查询中没有使用双引号时，搜索引擎会将输入的词语视为单独的搜索项，并返回包含这些词语的文档，无论这些词语是否以短语的形式出现。这意味着搜索结果可能包含只包含部分查询词语的文档，或者词语的顺序与查询中不同。例如，如果要查找包含“apple”和“pie”的文档，但不要求它们作为一个短语出现，可以在搜索框中输入：apple pie（不使用双引号）。这样，搜索引擎就会返回所有包含“apple”和“pie”这两个词语的文档，无论它们是否相邻或顺序如何。

**总结**

因此，在KQL中，使用双引号可以提高搜索的精确性，确保只检索到包含完整短语的文档；而没有使用双引号则会扩大搜索范围，可能检索到包含部分查询词语的文档。根据具体需求选择合适的搜索方式，可以更有效地获取所需信息。



匹配 name 有 apple 或者 pie 的关键词的文档

```
name:apple pie
```

匹配 name 为 apple pie 的文档

```
name:"apple pie"
```



