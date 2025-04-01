package com.future.demo;

import com.future.demo.component.MyComponent1;
import com.future.demo.controller.MyController1;
import com.future.demo.dao.MyDao1;
import com.future.demo.service.MyService1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest
class DemoSpringBootIocDiApplicationTests {

    // 注入ConfigurableApplicationContext实例
    @Autowired
    private ConfigurableApplicationContext ioc;
    @Autowired
    MyBean6Config myBean6Config;

    @Test
    void contextLoads() throws InterruptedException {
        // 查看ioc容器中有那些bean
        String[] names = ioc.getBeanDefinitionNames();
        List<String> nameList = Arrays.asList(names);
        Assertions.assertTrue(nameList.contains("org.springframework.context.annotation.internalConfigurationAnnotationProcessor"));

        // 验证注册的myBean1和myBean11是否存在
        Assertions.assertTrue(nameList.contains("myBean1"));
        Assertions.assertTrue(nameList.contains("myBean11"));

        // 验证通过@Bean指定的bean名称
        Assertions.assertTrue(nameList.contains("myBean12"));

        // region 根据bean名称获取bean实例

        // bean不存在
        try {
            ioc.getBean("noneExists");
            Assertions.fail("预期异常没有抛出");
        } catch (NoSuchBeanDefinitionException e) {
            Assert.isInstanceOf(NoSuchBeanDefinitionException.class, e);
        }

        // bean存在
        Assertions.assertNotNull(ioc.getBean("myBean1"));

        // endregion

        // region 根据bean类型获取bean实例

        // bean不存在
        try {
            ioc.getBean(String.class);
            Assertions.fail("预期异常没有抛出");
        } catch (NoSuchBeanDefinitionException e) {
            Assert.isInstanceOf(NoSuchBeanDefinitionException.class, e);
        }

        // 不是唯一的bean
        try {
            ioc.getBean(MyBean1.class);
            Assertions.fail("预期异常没有抛出");
        } catch (NoUniqueBeanDefinitionException ex) {
            Assert.isInstanceOf(NoUniqueBeanDefinitionException.class, ex);
        }

        Assertions.assertFalse(ioc.getBeansOfType(MyBean1.class).isEmpty());

        Assertions.assertNotNull(ioc.getBean("myBean1", MyBean1.class));

        // endregion

        // bean默认是提前初始化并且是单实例的
        MyBean1 myBean1 = (MyBean1) ioc.getBean("myBean1");
        MyBean1 myBean2 = (MyBean1) ioc.getBean("myBean1");
        Assertions.assertSame(myBean1, myBean2);

        MyBean6 myBean60 = (MyBean6) ioc.getBean("myBean61");
        MyBean6 myBean61 = myBean6Config.myBean61();
        Assertions.assertSame(myBean60, myBean61);

        // 测试@Configuration配置类配置的myBean2
        Assertions.assertNotNull(ioc.getBean("myBean2"));

        // 使用mvc分层注解@Controller、@Service、@Repository、@Component直接注册bean，比@Configuration+@Bean更简单
        // @Controller、@Service、@Repository注解实质是@Component注解
        // @Controller用于注解控制器类，@Service用于注解服务类，@Repository用于注解数据访问类
        // @Component用于注解除控制器、服务类、数据访问类外的普通类
        Assertions.assertNotNull(ioc.getBean(MyController1.class));
        Assertions.assertNotNull(ioc.getBean(MyService1.class));
        Assertions.assertNotNull(ioc.getBean(MyDao1.class));
        Assertions.assertNotNull(ioc.getBean(MyComponent1.class));

        // 测试@ComponentScan注解扫描到的bean
        // 默认情况下Spring只扫描@SpringBootApplication所在的包及其子包下的注解，如果需要扫描其他包，可以使用@ComponentScan注解指定扫描的包
        Assertions.assertNotNull(ioc.getBean("myBean3"));

        // region 测试@Scope注解

        // prototype作用域的bean是每次请求都会创建一个新的实例，并且不会被缓存
        Assertions.assertNotSame(ioc.getBean("myBean2"), ioc.getBean("myBean2"));

        // singleton作用域的bean是每次请求都会返回同一个实例，并且会被缓存
        Assertions.assertSame(ioc.getBean("myBean13"), ioc.getBean("myBean13"));

        // endregion

        // region 测试@Lazy

        Thread.sleep(1000);

        // singleton作用域的bean默认是非懒加载的，即在容器启动时就创建好，并缓存起来
        Assertions.assertTrue(new Date().getTime() - ioc.getBean("myBean41", MyBean4.class).getCreateTime().getTime() >= 1000);

        // singleton作用域并标注@Lazy的bean是懒加载的，即在第一次请求时才创建好，并缓存起来
        Assertions.assertTrue(new Date().getTime() - ioc.getBean("myBean42", MyBean4.class).getCreateTime().getTime() <= 1);

        // endregion

        // 测试FactoryBean
        Assertions.assertSame(ioc.getBean(MyBean5.class), ioc.getBean(MyBean5.class));

        // region @Autowired用法

        // 测试根据类型@Autowired注入
        Assertions.assertNotNull(ioc.getBean(MyService1.class).xxx);

        // 先根据类型@Autowired注入，再根据名称@Autowired注入，如果没有找到，则抛出异常
        Assertions.assertNotNull(ioc.getBean(MyService1.class).myBean41);

        // 根据类型注入多个bean
        Assertions.assertEquals(4, ioc.getBean(MyService1.class).myBean1List.size());
        Assertions.assertEquals(4, ioc.getBean(MyService1.class).myBean1Map.size());
        Assertions.assertTrue(ioc.getBean(MyService1.class).myBean1Map.containsKey("myBean11"));

        // 注入spring ioc容器
        Assertions.assertSame(ioc, ioc.getBean(MyService1.class).ioc);

        // endregion

        // @Qulifier注解用法，精确指定根据bean名称注入
        // 在有@Primary指定默认的bean时，@Autowired会自动注入此bean，此时使用@Qulifier注解指定注入的bean名称以忽略@Primary注解
        Assertions.assertSame(ioc.getBean(MyService1.class).xxx2, ioc.getBean("myBean62"));

        // 测试构造器注入
        Assertions.assertNotNull(ioc.getBean(MyDao1.class).getMyBean7());

        // 测试setter方法注入
        Assertions.assertNotNull(ioc.getBean(MyDao1.class).getMyBean8());

        // 测试XxxAware感知接口
        String osType = ioc.getBean(MyBean8.class).getOsType();
        Assertions.assertEquals(System.getProperty("os.name"), osType);
        String beanName = ioc.getBean(MyBean8.class).getBeanName();
        Assertions.assertEquals("myBean8", beanName);

        // 测试@Value
        String name = ioc.getBean(MyBean9.class).getName();
        Assertions.assertEquals("测试", name);
        String id = ioc.getBean(MyBean9.class).getId();
        Assertions.assertNotNull(id);

        // 测试bean生命周期方法init和destroy
        MyBeanLifeCycle myBeanLifeCycle = ioc.getBean(MyBeanLifeCycle.class);
        Assertions.assertTrue(myBeanLifeCycle.isInit);
        //ioc.close();
        //Assertions.assertFalse(myBeanLifeCycle.isInit);
        // 测试InitializingBean接口
        Assertions.assertTrue(myBeanLifeCycle.invokedAfterPropertiesSet);
        // 测试@PostConstruct
        Assertions.assertTrue(myBeanLifeCycle.invokedPostConstruct);

        // 测试BeanPostProcessor
        // 具体使用案例参考spring框架中AutowiredAnnotationBeanPostProcessor
        String name1 = ioc.getBean(MyBean10.class).getName();
        Assertions.assertEquals("hello myBean10", name1);
    }

}
