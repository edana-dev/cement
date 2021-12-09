package com.maneki.cement.config.boot;

import com.maneki.cement.config.core.ConfigManager;
import com.maneki.cement.config.core.ConfigProperties;
import com.maneki.cement.config.core.ConfigRefresher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ConfigProperties.class)
@ConditionalOnProperty(
        name = {"spring.cloud.db.config.enabled"},
        matchIfMissing = true
)
public class DBConfigAutoConfiguration {
    @Bean
    public ConfigRefresher dbConfigRefresher(ConfigManager configManager) {
        return new ConfigRefresher(configManager);
    }
}
