# `QueryWrapper`的用法

## 查询返回指定列

> 请参考例子 [链接](https://github.com/dexterleslie1/demonstration/blob/master/demo-mybatis/demo-spring-boot-mybatis-plus/src/test/java/com/future/demo/mybatis/plus/mapper/UserMapperTests.java#L49)

```java
QueryWrapper<User> queryWrapper = Wrappers.query();
queryWrapper.select("id", "name", "age");
queryWrapper.eq("name", name);
queryWrapper.orderByAsc("id");
List<Map<String, Object>> mapList = userMapper.selectMaps(queryWrapper);
Assert.assertEquals(1, mapList.size());
Assert.assertEquals(name, mapList.get(0).get("name"));
Assert.assertEquals(18, mapList.get(0).get("age"));
```



