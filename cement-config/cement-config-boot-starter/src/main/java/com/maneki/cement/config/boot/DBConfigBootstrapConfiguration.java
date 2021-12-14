package com.maneki.cement.config.boot;

import com.maneki.cement.config.core.ConfigManager;
import com.maneki.cement.config.core.ConfigProperties;
import com.maneki.cement.config.core.ConfigPropertySourceLocator;
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
public class DBConfigBootstrapConfiguration {

    @Bean
    public ConfigManager dbConfigManager(ConfigProperties properties) {
        return new ConfigManager(properties);
    }

    @Bean
    public ConfigPropertySourceLocator samplePropertySourceLocator(ConfigManager configManager) {
        return new ConfigPropertySourceLocator(configManager);
    }
}
