package cn.edana.cement.dbconfig.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

@Data
@ConfigurationProperties(DBConfigProperties.PREFIX)
public class DBConfigProperties {
    public static final String PREFIX = "spring.cloud.db.config";

    /**
     * 默认属性查询SQL
     * name: 属性名称
     * type: 属性类型，包括：json, string, boolean, int
     * value: 属性值
     */
    public static final String DEFAULT_SQL = "select name, type, value from cement_properties";

    @Autowired
    @JsonIgnore
    @Setter
    private Environment environment;

    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private String sql;

    @PostConstruct
    public void init() {
        this.overrideFromEnv();
    }

    private void overrideFromEnv() {
        if (StringUtils.isEmpty(this.getUrl())) {
            this.setUrl(environment
                    .resolvePlaceholders("${spring.cloud.db.config.url:}"));
        }
        if (StringUtils.isEmpty(this.getUsername())) {
            this.setUsername(
                    environment.resolvePlaceholders("${spring.cloud.db.config.username:}"));
        }
        if (StringUtils.isEmpty(this.getPassword())) {
            this.setPassword(
                    environment.resolvePlaceholders("${spring.cloud.db.config.password:}"));
        }
        if (StringUtils.isEmpty(this.getDriverClassName())) {
            this.setDriverClassName(
                    environment.resolvePlaceholders("${spring.cloud.db.config.driver-class-name:}"));
        }
        if (StringUtils.isEmpty(this.getSql())) {
            String sql = environment.resolvePlaceholders("${spring.cloud.db.config.sql:}");
            if (StringUtils.isEmpty(sql)) {
                sql = DEFAULT_SQL;
            }
            this.setSql(sql);
        }
    }
}
