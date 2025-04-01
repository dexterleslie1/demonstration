package com.future.demo;

import com.future.common.exception.BusinessException;
import com.future.common.http.ListResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * mybatis-plus相关的sql注入
 */
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class MybatisPlusSqlInjectionController {
    @Autowired
    UserMapper userMapper;

    /**
     * 演示mybatis ${}参数的sql注入
     *
     * @param username
     * @return
     * @throws SQLException
     * @throws BusinessException
     */
    @GetMapping("mybatis-plus/testSqlInjection")
    ListResponse<String> testMybatisPlusSqlInjection(
            @RequestParam(name = "username", defaultValue = "") String username
    ) throws SQLException, BusinessException {
        List<User> userList = this.userMapper.getByUsername(username);
        return ResponseUtils.successList(userList.stream().map(o -> o.getUsername()).collect(Collectors.toList()));
    }

    /**
     * where in sql注入
     *
     * @param idList
     * @return
     * @throws SQLException
     * @throws BusinessException
     */
    @GetMapping("mybatis-plus/testWhereInSqlInjection")
    ListResponse<String> testWhereInSqlInjection(
            @RequestParam(name = "idList", required = false) List<String> idList
    ) throws SQLException, BusinessException {
        List<User> userList = this.userMapper.listByIds(idList);
        return ResponseUtils.successList(userList.stream().map(o -> o.getUsername()).collect(Collectors.toList()));
    }

    /**
     * 协助存储过程sql注入测试
     *
     * @param username
     * @return
     */
    @GetMapping("mybatis-plus/testProcedureSqlInjection")
    ListResponse<String> testMybatisPlusProcedureSqlInjection(
            @RequestParam(name = "username", defaultValue = "") String username) {
        List<User> userList = this.userMapper.getByUsernameViaProcedure(username);
        return ResponseUtils.successList(userList.stream().map(o -> o.getUsername()).collect(Collectors.toList()));
    }
}
