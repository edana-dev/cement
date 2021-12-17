package com.maneki.cement.config.boot;

import com.maneki.cement.config.core.ConfigHealthIndicator;
import com.maneki.cement.config.core.ConfigManager;
import com.maneki.cement.config.core.ConfigProperties;
import com.maneki.cement.config.core.ConfigRefresher;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ConfigProperties.class)
@ConditionalOnProperty(
        name = {"spring.cloud.cement.config.enabled"},
        matchIfMissing = true
)
public class DBConfigAutoConfiguration {
    @Bean
    public ConfigRefresher dbConfigRefresher(ConfigManager configManager) {
        return new ConfigRefresher(configManager);
    }

    @Bean
    @ConditionalOnMissingBean(ConfigHealthIndicator.class)
    @ConditionalOnEnabledHealthIndicator("cement-config")
    public ConfigHealthIndicator dataSourceHealthIndicator(ConfigManager configManager) {
        return new ConfigHealthIndicator(configManager, "select 1");
    }
}
