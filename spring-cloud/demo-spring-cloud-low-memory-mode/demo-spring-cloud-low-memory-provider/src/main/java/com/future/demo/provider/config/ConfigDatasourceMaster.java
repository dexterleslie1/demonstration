package com.future.demo.provider.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(
        // 配置Java mapper所在包
        basePackages = "com.future.demo.provider.mapper",
        sqlSessionFactoryRef = "sqlSessionFactoryMaster",
        sqlSessionTemplateRef = "sqlSessionTemplateMaster")
public class ConfigDatasourceMaster {

    @Bean(name = "dataSourceMaster")
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 主库 SqlSessionFactory。
     * <p>
     * 必须使用 {@link MybatisSqlSessionFactoryBean}，而不是 MyBatis 原生的 {@code SqlSessionFactoryBean}。
     * Mapper 继承 MyBatis-Plus 的 {@code BaseMapper} 时，{@code selectList} 等 CRUD 语句由 MP 在工厂初始化时注入；
     * 若使用原生 Bean，则不会出现这些 MappedStatement，运行时会报
     * {@code Invalid bound statement (not found): xxxMapper.selectList}。
     * </p>
     */
    @Bean(name = "sqlSessionFactoryMaster")
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("dataSourceMaster") DataSource dataSource,
            ObjectProvider<MybatisPlusInterceptor> mybatisPlusInterceptorProvider) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        // MP 的工厂要求使用 MybatisConfiguration，不能传入 MyBatis 原生的 Configuration（否则编译/运行不兼容）
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(false);
        bean.setConfiguration(configuration);

        // 分页等插件（与 ConfigMybatisPlus 中定义的 Bean 一致，避免工厂未注册插件）
        mybatisPlusInterceptorProvider.ifAvailable(bean::setPlugins);

        // 配置mapper位置
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*Mapper.xml"));
        return bean.getObject();
    }

    @Bean(name = "sqlSessionTemplateMaster")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactoryMaster") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("dataSourceMaster") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
