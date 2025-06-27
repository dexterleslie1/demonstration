package com.future.demo.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class Dbcp2ConfigLogger implements CommandLineRunner {

    private final DataSource dataSource;

    public Dbcp2ConfigLogger(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        if (dataSource instanceof BasicDataSource) {
            BasicDataSource basicDataSource = (BasicDataSource) dataSource;
            System.out.println("DBCP2 Configuration:");
            System.out.println("  Driver Class Name: " + basicDataSource.getDriverClassName());
            System.out.println("  URL: " + basicDataSource.getUrl());
            System.out.println("  Username: " + basicDataSource.getUsername());
            System.out.println("  Initial Size: " + basicDataSource.getInitialSize());
            System.out.println("  Max Total: " + basicDataSource.getMaxTotal());
            System.out.println("  Max Idle: " + basicDataSource.getMaxIdle());
            System.out.println("  Min Idle: " + basicDataSource.getMinIdle());
            System.out.println("  Validation Query: " + basicDataSource.getValidationQuery());
            // Add more properties as needed
        } else {
            System.out.println("Datasource is not an instance of BasicDataSource.");
        }
    }
}
