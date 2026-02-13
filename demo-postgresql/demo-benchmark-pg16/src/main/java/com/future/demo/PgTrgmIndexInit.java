package com.future.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 应用启动时创建 pg_trgm 扩展及 name 列的 GIN 索引，
 * 用于优化 cloth_goods 表上 name LIKE '%...%' 的查询（如 queryByCompanyIdAndNameWildcard）。
 */
@Component
public class PgTrgmIndexInit {

    private static final Logger log = LoggerFactory.getLogger(PgTrgmIndexInit.class);

    @Resource
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        try {
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS pg_trgm");
            log.info("pg_trgm extension ensured");
        } catch (Exception e) {
            log.warn("pg_trgm extension create skipped or failed: {}", e.getMessage());
        }
        try {
            jdbcTemplate.execute(
                    "CREATE INDEX IF NOT EXISTS idx_cloth_goods_name_trgm ON cloth_goods USING GIN (name gin_trgm_ops)");
            log.info("GIN index idx_cloth_goods_name_trgm on cloth_goods(name) ensured");
        } catch (Exception e) {
            log.warn("GIN index idx_cloth_goods_name_trgm create skipped or failed: {}", e.getMessage());
        }
    }
}
