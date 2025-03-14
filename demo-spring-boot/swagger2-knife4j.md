# `Swagger2`、`Knife4j`



## `Swagger2`和`Knife4j`

Swagger2和Knife4j都是API文档生成工具，它们在软件开发中扮演着重要的角色，以下是关于Swagger2和Knife4j的详细介绍：

**Swagger2**

1. **定义与背景**
   - Swagger2是一种用于设计、构建和文档化RESTful API的工具。它使用简单的注解来描述API的结构和功能，从而帮助开发者生成交互式的API文档。Swagger最初是由Tony Tam在2010年创建的，旨在解决RESTful API的文档化和调试问题。随着RESTful API的普及，Swagger逐渐发展成为Swagger2，并成为了业界标准之一。
2. **特性与组件**
   - **及时性**：接口变更后，能够及时准确地通知相关前后端开发人员。
   - **规范性**：保证接口的规范性，如接口的地址、请求方式、参数及响应格式和错误信息。
   - **一致性**：接口信息一致，不会出现因开发人员拿到的文档版本不一致而出现分歧的情况。
   - **可测性**：可以直接在接口文档上进行测试，以方便理解业务。
   - Swagger2包含多个组件，如Swagger Editor（基于浏览器的编辑器，可以编写OpenAPI规范并实时预览）、Swagger UI（将OpenAPI规范呈现为交互式API文档）、Swagger Codegen（将OpenAPI规范生成为服务器存根和客户端库）等。
3. **常用注解**
   - @Api：将类标记为Swagger资源。
   - @ApiImplicitParam：表示API操作中的单个参数。
   - @ApiImplicitParams：允许多个@ApiImplicitParam对象列表的包装器。
   - @ApiModel：提供有关Swagger模型的其他信息。
   - @ApiModelProperty：添加和操作模型属性的数据。
   - @ApiOperation：描述针对特定路径的操作或通常是HTTP方法。
   - @ApiParam：为操作参数添加额外的元数据。
   - @ApiResponse：描述操作的可能响应。
   - @ApiResponses：允许多个@ApiResponse对象列表的包装器。
   - @Authorization：声明要在资源或操作上使用的授权方案。
   - @AuthorizationScope：描述OAuth2授权范围。

**Knife4j**

1. **定义与功能**
   - Knife4j是一个为Java MVC框架集成Swagger生成API文档的增强解决方案。它基于Swagger进行扩展，提供了更丰富的功能和更美观的用户界面。Knife4j可以帮助开发者轻松创建和测试API接口文档，生成的文档还可以导出给前端开发团队使用。
2. **优点**
   - **功能强大**：Knife4j集成了Swagger的所有功能，并提供了更多的定制选项和增强功能。
   - **易于操作**：Knife4j的UI界面非常美观且使用流畅，降低了操作难度。
   - **高度定制化**：Knife4j可以根据项目需求进行高度定制化，提升用户体验。
   - **良好的支持性**：Knife4j拥有庞大的社区支持和丰富的资源，方便开发者学习和使用。
3. **使用方式**
   - 在SpringBoot项目的pom.xml文件中添加Knife4j的依赖。
   - 创建配置类，配置Swagger的相关信息，如文档标题、描述、版本等。
   - 在控制器类和方法上添加Swagger注解，描述API的功能和参数。
   - 启动SpringBoot项目，访问指定的URL（如http://localhost:8080/doc.html）查看生成的Knife4j接口文档。

**Swagger2与Knife4j的结合使用**

Swagger2可以自动生成API文档，减少了手动编写文档的工作量，同时保证了文档的及时更新。而Knife4j则提供了更美观、更易于使用的用户界面和更多的定制选项。因此，将Swagger2与Knife4j结合使用可以极大地提升API文档的质量和用户体验。

综上所述，Swagger2和Knife4j都是非常重要的API文档生成工具。Swagger2提供了强大的API文档化功能，而Knife4j则在此基础上进行了增强和优化。开发者可以根据自己的需求选择合适的工具来生成和管理API文档。



## `Swagger2`



### SpringBoot 项目集成 Swagger2

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-swagger)

`SpringBoot`项目中添加如下依赖

```xml
<!-- swagger3依赖 -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

`Application`类添加`@EnableOpenApi`注解启用`Swagger2`

```java
@SpringBootApplication
@EnableOpenApi
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

新增`Swagger`配置类

```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     *
     * @return
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                // 隐藏默认Http code
                // https://github.com/springfox/springfox/issues/632
                .useDefaultResponseMessages(false)
                .apiInfo(createApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com"))//扫描com路径下的api文档
                .paths(PathSelectors.any())//路径判断
                .build()/*.alternateTypeRules(AlternateTypeRules.newRule(typeResolver.resolve(BaseResponse.class),
                                typeResolver.resolve(Pagination.class, UserModel.class)),
                        AlternateTypeRules.newRule(typeResolver.resolve(BaseResponse.class),
                                typeResolver.resolve(Pagination.class, SwaggerModel.class)))*/;
    }

    /**
     *
     * @return
     */
    private ApiInfo createApiInfo() {
        return new ApiInfoBuilder()
                .title("demo系统标题")//标题
                .description("demo系统描述")
                .version("1.0.0")//版本号
                .build();
    }


}

```

通过`@ApiModel`、`@ApiModelProperty`、`@Api`、`@ApiOperation`、`@ApiParam`、`@ApiResponse`等注解创建`Swagger`文档

启动`SpringBoot`应用，访问`http://localhost:8080/swagger-ui/index.html`查看`Swagger`文档



注意：Spring Security 如果拦截 Swagger URL，则使用下面配置放行：

```java
.authorizeRequests()
// 放行指定uri
.antMatchers(
        // 允许swagger2
        "/swagger-ui/**",
        // 允许swagger2
        "/swagger-resources/**",
        // 允许swagger2
        "/v2/api-docs").permitAll()
.anyRequest().authenticated()
```



注意：SpringBoot2.6 和 Swagger 集成报错解决办法

>[SpringBoot+Swagger整合（SpringBoot 2.6版本）](https://blog.csdn.net/Liujiaxinqq/article/details/130611388)

- 报错信息如下：

  ```java
  org.springframework.context.ApplicationContextException: Failed to start bean 'documentationPluginsBootstrapper'; nested exception is java.lang.NullPointerException
  	at org.springframework.context.support.DefaultLifecycleProcessor.doStart(DefaultLifecycleProcessor.java:181)
  	at org.springframework.context.support.DefaultLifecycleProcessor.access$200(DefaultLifecycleProcessor.java:54)
  	at org.springframework.context.support.DefaultLifecycleProcessor$LifecycleGroup.start(DefaultLifecycleProcessor.java:356)
  	at java.lang.Iterable.forEach(Iterable.java:75)
  	at org.springframework.context.support.DefaultLifecycleProcessor.startBeans(DefaultLifecycleProcessor.java:155)
  	at org.springframework.context.support.DefaultLifecycleProcessor.onRefresh(DefaultLifecycleProcessor.java:123)
  	at org.springframework.context.support.AbstractApplicationContext.finishRefresh(AbstractApplicationContext.java:937)
  	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:586)
  	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:145)
  	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:745)
  	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:423)
  	at org.springframework.boot.SpringApplication.run(SpringApplication.java:307)
  	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1317)
  	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1306)
  	at com.future.auth.ApplicationAuth.main(ApplicationAuth.java:15)
  Caused by: java.lang.NullPointerException: null
  	at springfox.documentation.spring.web.WebMvcPatternsRequestConditionWrapper.getPatterns(WebMvcPatternsRequestConditionWrapper.java:56)
  	at springfox.documentation.RequestHandler.sortedPaths(RequestHandler.java:113)
  	at springfox.documentation.spi.service.contexts.Orderings.lambda$byPatternsCondition$3(Orderings.java:89)
  	at java.util.Comparator.lambda$comparing$77a9974f$1(Comparator.java:469)
  	at java.util.TimSort.countRunAndMakeAscending(TimSort.java:355)
  	at java.util.TimSort.sort(TimSort.java:220)
  	at java.util.Arrays.sort(Arrays.java:1512)
  	at java.util.ArrayList.sort(ArrayList.java:1464)
  	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:387)
  	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
  	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
  	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
  	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
  	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
  	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
  	at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
  	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
  	at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499)
  	at springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider.requestHandlers(WebMvcRequestHandlerProvider.java:81)
  	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193)
  	at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1384)
  	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
  	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
  	at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
  	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
  	at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499)
  	at springfox.documentation.spring.web.plugins.AbstractDocumentationPluginsBootstrapper.withDefaults(AbstractDocumentationPluginsBootstrapper.java:107)
  	at springfox.documentation.spring.web.plugins.AbstractDocumentationPluginsBootstrapper.buildContext(AbstractDocumentationPluginsBootstrapper.java:91)
  	at springfox.documentation.spring.web.plugins.AbstractDocumentationPluginsBootstrapper.bootstrapDocumentationPlugins(AbstractDocumentationPluginsBootstrapper.java:82)
  	at springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper.start(DocumentationPluginsBootstrapper.java:100)
  	at org.springframework.context.support.DefaultLifecycleProcessor.doStart(DefaultLifecycleProcessor.java:178)
  	... 14 common frames omitted
  ```

- application.properties 配置如下：

  ```properties
  # SpringBoot2.6和Swagger集成报错解决办法
  # https://blog.csdn.net/Liujiaxinqq/article/details/130611388
  spring.mvc.pathmatch.matching-strategy=ant_path_matcher
  ```




### 配置 Enum（枚举）类型 Swagger 文档

>提醒：尝试 [链接](https://www.google.com/search?channel=fs&client=ubuntu&q=java+swagger+enum) 多种方式配置 Enum Swagger 文档都不起作用，暂时放弃。



## `Knife4j`

>[`Knife4j`官方参考资料](https://doc.xiaominfo.com/)
>
>[`SpringBoot3`配置`Knife4j`参考](https://doc.xiaominfo.com/docs/quick-start)
>
>`Knife4j`具体配置和用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-knife4j)

启动`SpringBoot`应用后，访问`http://localhost:8080/doc.html`查看`Knife4j`文档