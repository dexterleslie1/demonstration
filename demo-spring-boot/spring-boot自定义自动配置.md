# `spring-boot`自定义自动配置

> Spring Boot自动配置能够根据应用程序所使用的框架、库或环境条件，自动配置相应的功能和组件。例如，当应用程序添加了数据库相关的依赖时，Spring Boot可以自动配置数据库连接池、事务管理等组件，无需手动配置。

- 创建插件

  > 创建插件的例子详细请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-boot-autoconfiguration/spring-boot-starter-demo-plugin)

  定义自定义插件的自动配置类`DemoPluginAutoConfiguration`，自动配置类主要通过`@Bean`注解配置插件的相关`Bean`

  ```java
  // 当 TestPropertiesPrinter 类存在时，加载该配置类
  @ConditionalOnClass(value = TestPropertiesPrinter.class)
  // 启用自定义属性
  @EnableConfigurationProperties(value = TestPropertiesPrinter.TestProperties.class)
  public class DemoPluginAutoConfiguration {
      @Bean
      @ConditionalOnMissingBean
      TestPropertiesPrinter testPropertiesPrinter() {
          return new TestPropertiesPrinter();
      }
  }
  ```

  `TestPropertiesPrinter`

  ```java
  public class TestPropertiesPrinter {
      @Autowired
      TestProperties testProperties;
  
      public String print() {
          // 获取自定义属性的前缀
          ConfigurationProperties configurationProperties =
                  TestProperties.class.getAnnotation(ConfigurationProperties.class);
          String prefix = configurationProperties.prefix();
  
          return prefix + ".prop1=" + testProperties.getProp1();
      }
  
      @Data
      @ConfigurationProperties(prefix = "com.future.demo.test")
      public static class TestProperties {
          private String prop1;
          private int prop2;
          private List<String> prop3;
          private Map<String, String> prop4;
          private NestedTestProperties nested;
  
          @Data
          public static class NestedTestProperties {
              private String prop1;
              private int prop2;
          }
      }
  }
  ```

  配置`spring`自动扫描入口并自动配置插件的`/src/main/resources/META-INF/spring.factories`

  ```properties
  # 引入依赖后spring会自动扫描这个入口自动配置插件
  org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
    com.future.demo.starter.DemoPluginAutoConfiguration
  ```

  在本地`maven`发布插件

  ```bash
  mvn package -Dmaven.test.skip install
  ```

- 调用自动配置插件

  > 调用插件的例子详细请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-boot-autoconfiguration/spring-boot-tester)

  在`maven`中配置插件依赖

  ```xml
  <dependency>
      <groupId>com.future.demo</groupId>
      <artifactId>spring-boot-starter-demo-plugin</artifactId>
      <version>1.0.0</version>
  </dependency>
  ```

  `application.properties`中配置插件的自定义属性

  ```properties
  com.future.demo.test.prop1=astring1
  com.future.demo.test.prop2=18080
  com.future.demo.test.prop3[0]=av1
  com.future.demo.test.prop3[1]=av2
  com.future.demo.test.prop4.k1=avv1
  com.future.demo.test.prop4.k2=avv2
  com.future.demo.test.nested.prop1=anv1
  com.future.demo.test.nested.prop2=18090
  ```

  测试插件的功能

  ```java
  @Slf4j
  @RunWith(SpringRunner.class)
  @SpringBootTest(classes={Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
  public class ApplicationTests {
      @Autowired
      TestPropertiesPrinter testPropertiesPrinter;
  
      @Test
      public void test() {
          String str = testPropertiesPrinter.print();
          log.debug(str);
      }
  }
  ```

- 总结

  上面例子，只需要`maven`中配置插件即可以在测试类中通过`@Autowired`注入自动配置的`Bean TestPropertiesPrinter testPropertiesPrinter;`

