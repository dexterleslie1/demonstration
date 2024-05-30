# `maven`项目中配置`mockito`

## 普通`maven`项目中配置`mockito`

> 在普通`maven`项目中配置和使用`mockito`的详细请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-mockito/demo-mockito-basic)

在`pom.xml`添加`mockito`的依赖

```xml
<project>
    
    ...
    
	<dependencies>
		<!-- mockito依赖 -->
		<dependency>
			<groupId>org.mockito</groupId>
			<artifactId>mockito-all</artifactId>
			<version>1.9.5</version>
			<scope>test</scope>
		</dependency>
	</dependencies>
    
    ...
    
</project>

```

验证`mockito`是否成功引入代码

```java
@Test
public void verify_if_function_called(){
    mockListObject.add("val1");
    Mockito.verify(mockListObject).add("val1");
}
```



## `spring-boot`项目中配置`mockito`

> 在`spring-boot`项目中配置和使用`mockito`的详细请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-mockito/demo-mockito-springboot)
>
> 注意：`mockito`依赖已经包含在`org.springframework.boot:spring-boot-starter-test`中，所以不需要独立引入`mockito`依赖。
