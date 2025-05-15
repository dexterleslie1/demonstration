package com.future.demo.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@MapperScan(
        // 配置Java mapper所在包
        basePackages = "com.future.demo.mapperidca",
        sqlSessionFactoryRef = "idcaSqlSessionFactory",
        sqlSessionTemplateRef = "idcaSqlSessionTemplate")
public class DsIdcaConfig {

    @Bean(name = "idcaDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.idca")
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "idcaSqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("idcaDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        // 配置mapper位置
        // bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/idca/*Mapper.xml"));
        return bean.getObject();
    }

    @Bean(name = "idcaSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("idcaSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
