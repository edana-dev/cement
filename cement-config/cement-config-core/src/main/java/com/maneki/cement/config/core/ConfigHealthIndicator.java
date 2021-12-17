package com.maneki.cement.config.core;

import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.Status;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
public class ConfigHealthIndicator extends AbstractHealthIndicator {

    private ConfigManager configManager;
    private String query;
    private JdbcTemplate jdbcTemplate;

    public ConfigHealthIndicator(ConfigManager configManager, String query) {
        this.configManager = configManager;
        this.query = query;
    }

    protected void doHealthCheck(Builder builder) throws Exception {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        if (jdbcTemplate == null) {
            builder.unknown().withDetail("message", "datasource not initial");
            return;
        }
        builder.up().withDetail("database", this.getProduct(jdbcTemplate));
        String validationQuery = this.query;
        if (StringUtils.hasText(validationQuery)) {
            builder.withDetail("validationQuery", validationQuery);
            List<Object> results = jdbcTemplate.query(validationQuery, new SingleColumnRowMapper());
            Object result = DataAccessUtils.requiredSingleResult(results);
            builder.withDetail("result", result);
        } else {
            builder.withDetail("validationQuery", "isValid()");
            boolean valid = this.isConnectionValid(jdbcTemplate);
            builder.status(valid ? Status.UP : Status.DOWN);
        }

    }

    private JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate != null) {
            return jdbcTemplate;
        }
        synchronized (this) {
            if (jdbcTemplate != null) {
                return jdbcTemplate;
            }
            DataSource dataSource = configManager.getConfigService().getDataSource();
            if (dataSource == null) {
                return null;
            }
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        }
        return this.jdbcTemplate;
    }

    private String getProduct(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.execute((Connection conn) -> conn.getMetaData().getDatabaseProductName());
    }

    private Boolean isConnectionValid(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.execute((Connection conn) -> conn.isValid(0));
    }


    private static class SingleColumnRowMapper implements RowMapper<Object> {
        private SingleColumnRowMapper() {
        }

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            ResultSetMetaData metaData = rs.getMetaData();
            int columns = metaData.getColumnCount();
            if (columns != 1) {
                throw new IncorrectResultSetColumnCountException(1, columns);
            } else {
                return JdbcUtils.getResultSetValue(rs, 1);
            }
        }
    }
}