# 演示springboot项目使用mockito

## 注入logger对象

> **[mock-private-static-final-field-using-mockito-or-jmockit](https://stackoverflow.com/questions/30703149/mock-private-static-final-field-using-mockito-or-jmockit)**
> **[what-is-the-best-way-to-unit-test-slf4j-log-messages](https://stackoverflow.com/questions/4650222/what-is-the-best-way-to-unit-test-slf4j-log-messages/60988775#60988775)**

## @MockBean

> 自动创建 mock bean 并注入到 spring 容器中，自动替换所有同类型的 bean
> 参考 MockBeanTests

## @InjectMocks + @Mock

> 使用以上组合注入 @Mock 注解生成的 bean 到 @InjectMocks bean中
> 如果允许情况下建议使用 @MockBean 替换这个使用组合

## @InjectMocks + @Spy

> 和 @InjectMocks + @Mock 区别是，如果没有定义 mock 规则(Mockito.doReturn("param2=p2").when(this.myServiceInner).test2(Mockito.anyString());) 则  @Spy 注入的 bean 会执行原来没有被 mock 的代码逻辑(实现一个实例部分接口被 mock的目的)，而 @Mock 注入的 bean 没有定义 mock  规则只会返回默认值(String类型返回返回值为null，int类型返回返回值为0)。
> 如果允许情况下建议使用 @SpyBean 替换这个使用组合

## @SpyBean

> 自动创建 mock bean 并注入到 spring 容器中，自动替换所有同类型的 bean，没有被定义 mock 规则的方法默认执行原始逻辑。
> 参考 SpyBeanTests
