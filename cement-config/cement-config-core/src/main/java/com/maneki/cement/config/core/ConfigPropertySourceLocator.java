package com.maneki.cement.config.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

// Locate before Nacos Source Locator.
@Order(-1)
@Slf4j
public class ConfigPropertySourceLocator implements PropertySourceLocator {

    public static final String DB_CONFIG_PROPERTY_SOURCE_NAME = "db-config";

    private final ConfigManager configManager;

    public ConfigPropertySourceLocator(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public PropertySource<?> locate(Environment environment) {
        configManager.getProperties().setEnvironment(environment);
        ConfigService configService = configManager.getConfigService();

        if (null == configService) {
            log.warn("no instance of config service found, can't load config from db config");
            return null;
        }

        MapPropertySource mapPropertySource = new MapPropertySource(DB_CONFIG_PROPERTY_SOURCE_NAME, configService.getSource());
        log.info("Located property source: {}", DB_CONFIG_PROPERTY_SOURCE_NAME);
        return mapPropertySource;
    }
}
