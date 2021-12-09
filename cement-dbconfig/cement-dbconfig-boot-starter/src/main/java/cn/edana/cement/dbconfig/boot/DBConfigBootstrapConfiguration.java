package cn.edana.cement.dbconfig.boot;

import cn.edana.cement.dbconfig.core.DBConfigManager;
import cn.edana.cement.dbconfig.core.DBConfigProperties;
import cn.edana.cement.dbconfig.core.DBConfigPropertySourceLocator;
import cn.edana.cement.dbconfig.core.DBConfigRefresher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DBConfigProperties.class)
@ConditionalOnProperty(
        name = {"spring.cloud.db.config.enabled"},
        matchIfMissing = true
)
public class DBConfigBootstrapConfiguration {

    @Bean
    public DBConfigManager dbConfigManager(DBConfigProperties properties) {
        return new DBConfigManager(properties);
    }

    @Bean
    public DBConfigPropertySourceLocator samplePropertySourceLocator(DBConfigManager dbConfigManager) {
        return new DBConfigPropertySourceLocator(dbConfigManager);
    }
}
