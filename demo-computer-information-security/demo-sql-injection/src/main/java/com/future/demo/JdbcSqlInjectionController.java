package com.future.demo;

import com.future.common.constant.ErrorCodeConstant;
import com.future.common.exception.BusinessException;
import com.future.common.http.ListResponse;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * jdbc相关的sql注入
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class JdbcSqlInjectionController {
    @Autowired
    UserMapper userMapper;

    /**
     * 基于错误的sql注入
     * <p>
     * 例子中sql注入select 1 from table_test;目的是在探测数据库是否存在名为table_test的表，
     * 如果数据库的报告的错误信息不经过处理直接返回到界面，那么攻击这可以通过错误信息推测数据库的结构
     * <p>
     * 这种注入适用于有数据库错误信息返回到界面的情况，通过查看错误信息即可判断是否有执行注入的`sql`
     */
    @GetMapping("jdbc/testErrorBasedSqlInjection")
    ObjectResponse<String> testErrorBasedSqlInjection(
            @RequestParam(name = "username", defaultValue = "") String username
    ) throws SQLException, BusinessException {
        try {
            // 注意：因为要执行多条sql，所以在jdbc驱动中添加参数allowMultiQueries=true
            String url = "jdbc:mysql://localhost:3306/demo?allowMultiQueries=true";
            String user = "root";
            String password = "123456";
            String sql = "SELECT * FROM `user` WHERE username='" + username + "'";
            log.debug("恶意SQL: " + sql);

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                // ...
            }

            return ResponseUtils.failObject(ErrorCodeConstant.ErrorCodeCommon, "预期异常没有抛出");
        } catch (SQLSyntaxErrorException ex) {
            // Table 'demo.table_test' doesn't exist
            throw new BusinessException(ex.getMessage());
        }
    }

    /**
     * 布尔型盲SQL注入
     * <p>
     * 例子中注入 'OR '1'='1 是为了测试盲注是否成功，如果盲注成功会自动跳转到主界面，否则会跳回登录节目并提示帐号密码错误
     * <p>
     * 这种注入适用于没有数据库错误信息返回到界面的情况，通过接口返回状态判断是否执行注入的`sql`
     */
    @GetMapping("jdbc/testBooleanBasedBlindSqlInjection")
    ObjectResponse<String> testBooleanBasedBlindSqlInjection(
            @RequestParam(name = "username", defaultValue = "") String username
    ) throws SQLException, BusinessException {
        String url = "jdbc:mysql://localhost:3306/demo";
        String user = "root";
        String password = "123456";
        String sql = "SELECT * FROM `user` WHERE username='" + username + "'";
        log.debug("恶意SQL: " + sql);

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (!rs.next())
                // 盲注失败后会回登录界面并提示帐号密码错误
                return ResponseUtils.failObject(ErrorCodeConstant.ErrorCodeCommon, "跳转回登录界面并提示帐号密码错误");
            else
                // 盲注成功后会从登录界面跳转到主界面
                return ResponseUtils.successObject("从登录界面跳转到主界面");
        }
    }

    /**
     * 基于时间的盲SQL注入
     * <p>
     * 例子注入 ' OR IF(1=1, SLEEP(1), 0) OR '1'='0 为了使sql sleep 1秒，如果接口调用耗时超过1秒说明sql注入成功
     * <p>
     * 这种注入适用于没有数据库错误信息返回到界面的情况，通过接口调用耗时情况判断是否执行注入的`sql`
     */
    @GetMapping("jdbc/testTimeBasedBlindSqlInjection")
    ObjectResponse<Long> testTimeBasedBlindSqlInjection(
            @RequestParam(name = "username", defaultValue = "") String username
    ) throws SQLException, BusinessException {
        String url = "jdbc:mysql://localhost:3306/demo";
        String user = "root";
        String password = "123456";
        String sql = "SELECT * FROM `user` WHERE username='" + username + "'";
        log.debug("恶意SQL: " + sql);

        java.util.Date startTime = new java.util.Date();
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            java.util.Date endTime = new java.util.Date();
            return ResponseUtils.successObject(endTime.getTime() - startTime.getTime());
        }
    }

    /**
     * 联合查询SQL注入
     * <p>
     * 例子中借助UNION注入其他select读取secret_data的数据，这是意料之外的
     * <p>
     * 这中注入用于窃取非授权访问的数据。
     */
    @GetMapping("jdbc/testUnionBasedSqlInjection")
    ListResponse<String> testUnionBasedSqlInjection(
            @RequestParam(name = "username", defaultValue = "") String username
    ) throws SQLException, BusinessException {
        String url = "jdbc:mysql://localhost:3306/demo";
        String user = "root";
        String password = "123456";
        String sql = "SELECT * FROM `user` WHERE username='" + username + "'";
        log.debug("恶意SQL: " + sql);

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<String> userList = new ArrayList<>();
            while (rs.next()) {
                userList.add(rs.getString("username"));
            }
            return ResponseUtils.successList(userList);
        }
    }
}
