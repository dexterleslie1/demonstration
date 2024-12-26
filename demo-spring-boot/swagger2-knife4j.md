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

>`Swagger2`具体配置和用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-swagger`

`spring-boot`项目中添加如下依赖

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
    @Autowired
    private TypeResolver typeResolver;

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

启动`spring boot`应用，访问`http://localhost:8080/swagger-ui/index.html`查看`Swagger`文档



## `Knife4j`

>`Knife4j`官方参考资料`https://doc.xiaominfo.com/`
>
>`spring boot 3`配置`Knife4j`参考`https://doc.xiaominfo.com/docs/quick-start`
>
>`Knife4j`具体配置和用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-knife4j`

启动`spring boot`应用后，访问`http://localhost:8080/doc.html`查看`Knife4j`文档