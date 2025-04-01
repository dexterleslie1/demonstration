package com.future.demo.dao;

import com.future.demo.bean.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BookDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public Book get(Long id) {
        return jdbcTemplate.queryForObject("select * from book where id = ?", new Object[]{id}, new BeanPropertyRowMapper<>(Book.class));
    }

    public void updateStock(Long id, Integer stock) {
        jdbcTemplate.update("update book set stock=stock-? where id = ?", stock, id);
    }

    // 如果父级事务抛出异常，此事物不会回滚
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStockWithPropagationRequiresNew(Long id, Integer stock) {
        jdbcTemplate.update("update book set stock=stock-? where id = ?", stock, id);
    }

    public void updateSetStock(Long id, Integer stock) {
        jdbcTemplate.update("update book set stock=? where id = ?", stock, id);
    }
}
