package com.future.demo.dao;

import com.future.demo.bean.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Repository
public class AccountDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public Account get(Long id) {
        return this.jdbcTemplate.queryForObject("select * from account where id=?", new Object[]{id},
                new BeanPropertyRowMapper<>(Account.class));
    }

    // 读未提交隔离级别，存在脏读问题
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Account getWithIsolationReadUncommitted(Long id) {
        return this.jdbcTemplate.queryForObject("select * from account where id=?", new Object[]{id},
                new BeanPropertyRowMapper<>(Account.class));
    }

    // 读已提交隔离级别，解决脏读问题，但仍存在不可重复读问题
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Account[] getWithIsolationReadCommited(Long id) {
        Account account = this.jdbcTemplate.queryForObject("select * from account where id=?", new Object[]{id},
                new BeanPropertyRowMapper<>(Account.class));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Account account1 = this.jdbcTemplate.queryForObject("select * from account where id=?", new Object[]{id},
                new BeanPropertyRowMapper<>(Account.class));

        // 返回值为数组是为了配合演示读已提交隔离级别存在不可重复读问题
        return new Account[]{account, account1};
    }

    // 可重复读隔离级别，解决脏读，不可重复读问题
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Account[] getWithIsolationRepeatableRead(Long id) {
        Account account = this.jdbcTemplate.queryForObject("select * from account where id=?", new Object[]{id},
                new BeanPropertyRowMapper<>(Account.class));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Account account1 = this.jdbcTemplate.queryForObject("select * from account where id=?", new Object[]{id},
                new BeanPropertyRowMapper<>(Account.class));

        // 返回值为数组是为了配合演示可重复读隔离级别解决了不可重复读问题
        return new Account[]{account, account1};
    }


    public void updateBalance(Long id, BigDecimal total) {
        String sql = "update account set balance=balance-? where id=?";
        this.jdbcTemplate.update(sql, total, id);
    }

    // 此事务和父级事务是同一个事务，如果父级事务抛出异常，此事务回滚
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateBalanceWithPropagationRequired(Long id, BigDecimal total) {
        String sql = "update account set balance=balance-? where id=?";
        this.jdbcTemplate.update(sql, total, id);
    }

    public void updateSetBalance(Long id, BigDecimal balance) {
        String sql = "update account set balance=? where id=?";
        this.jdbcTemplate.update(sql, balance, id);
    }
}
