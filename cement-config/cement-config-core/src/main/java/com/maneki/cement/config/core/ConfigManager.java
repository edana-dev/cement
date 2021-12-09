package com.maneki.cement.config.core;

import lombok.Getter;

import java.util.Objects;

public class ConfigManager {

    @Getter
    private final ConfigProperties properties;

    private static ConfigService service;

    public ConfigManager(ConfigProperties properties) {
        this.properties = properties;
        createConfigService(properties);
    }

    /**
     * Compatible with old design,It will be perfected in the future.
     */
    static ConfigService createConfigService(
            ConfigProperties properties) {
        if (Objects.isNull(service)) {
            synchronized (ConfigManager.class) {
                if (Objects.isNull(service)) {
                    service = ConfigFactory.createConfigService(properties);
                }
            }
        }
        return service;
    }


    public ConfigService getConfigService() {
        if (Objects.isNull(service)) {
            createConfigService(this.properties);
        }
        return service;
    }
}
