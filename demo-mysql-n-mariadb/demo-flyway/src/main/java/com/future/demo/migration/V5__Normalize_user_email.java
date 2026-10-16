package com.future.demo.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring Boot 会收集 {@code JavaMigration} Bean 并注册到 Flyway。
 * 类名仍须是 {@code V{版本}__描述}，这样才会排进版本顺序。
 */
@Component
public class V5__Normalize_user_email extends BaseJavaMigration {

    private static final Logger log = LoggerFactory.getLogger(V5__Normalize_user_email.class);

    private final EmailNormalizer emailNormalizer;

    public V5__Normalize_user_email(EmailNormalizer emailNormalizer) {
        this.emailNormalizer = emailNormalizer;
    }

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        List<Long> ids = new ArrayList<Long>();
        List<String> emails = new ArrayList<String>();
        try (PreparedStatement select = connection.prepareStatement("SELECT id, email FROM user");
             ResultSet rs = select.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getLong("id"));
                emails.add(rs.getString("email"));
            }
        }

        int changed = 0;
        try (PreparedStatement update = connection.prepareStatement(
                "UPDATE user SET email = ? WHERE id = ?")) {
            for (int i = 0; i < ids.size(); i++) {
                String normalized = emailNormalizer.normalize(emails.get(i));
                if (normalized == null || normalized.equals(emails.get(i))) {
                    continue;
                }
                update.setString(1, normalized);
                update.setLong(2, ids.get(i));
                update.addBatch();
                changed++;
            }
            if (changed > 0) {
                update.executeBatch();
            }
        }
        log.info("V5 规范化 user.email 完成，更新 {} 行", changed);
    }
}
