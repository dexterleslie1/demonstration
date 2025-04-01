package com.future.demo;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

public class MyDataSourceInitializer2 extends DataSourceInitializer {
    public MyDataSourceInitializer2(DataSource dataSource) {
        this.setDataSource(dataSource);

        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        List<String> sqlFileList = Arrays.asList("db2.sql");
        if (sqlFileList != null && sqlFileList.size() != 0) {
            for (String filePath : sqlFileList) {
                ClassPathResource resource = new ClassPathResource(filePath);
                databasePopulator.addScript(resource);
            }
        }
        this.setDatabasePopulator(databasePopulator);

        // 清除数据库DatabasePopulator
        ResourceDatabasePopulator databaseCleaner = new ResourceDatabasePopulator();
        sqlFileList = null;
        if (sqlFileList != null && sqlFileList.size() != 0) {
            for (String filePath : sqlFileList) {
                ClassPathResource resource = new ClassPathResource(filePath);
                databasePopulator.addScript(resource);
            }
        }
        this.setDatabaseCleaner(databaseCleaner);
    }
}
