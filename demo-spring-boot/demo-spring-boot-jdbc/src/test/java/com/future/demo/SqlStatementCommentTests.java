package com.future.demo;

import org.junit.Test;

import java.sql.*;

public class SqlStatementCommentTests {
    @Test
    public void testMariaDBSqlStatemntComment() throws SQLException {
        String connectionString = "jdbc:mysql://localhost:50000/demo_db";
        try (Connection connection = DriverManager.getConnection(connectionString, "root", "123456");
             Statement statement = connection.createStatement();
             // 使用 -- 注释sql语句
             ResultSet resultSet = statement.executeQuery("select * from operation_log where content='' OR 1=1 -- '")) {

        }

        connectionString = "jdbc:mysql://localhost:50000/demo_db";
        try (Connection connection = DriverManager.getConnection(connectionString, "root", "123456");
             Statement statement = connection.createStatement();
             // 使用 # 注释sql语句
             ResultSet resultSet = statement.executeQuery("select * from operation_log where content='' OR 1=1 #'")) {

        }
    }

    @Test
    public void testMySQLSqlStatementComment() throws SQLException {
        String connectionString = "jdbc:mysql://localhost:50001/demo_db";
        try (Connection connection = DriverManager.getConnection(connectionString, "root", "123456");
             Statement statement = connection.createStatement();
             // 使用 -- 注释sql语句
             ResultSet resultSet = statement.executeQuery("select * from operation_log where content='' OR 1=1 -- '")) {

        }

        connectionString = "jdbc:mysql://localhost:50001/demo_db";
        try (Connection connection = DriverManager.getConnection(connectionString, "root", "123456");
             Statement statement = connection.createStatement();
             // 使用 # 注释sql语句
             ResultSet resultSet = statement.executeQuery("select * from operation_log where content='' OR 1=1 #'")) {

        }
    }
}
