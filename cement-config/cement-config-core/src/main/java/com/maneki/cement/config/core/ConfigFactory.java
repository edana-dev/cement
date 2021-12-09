package com.maneki.cement.config.core;

public class ConfigFactory {
    public static ConfigService createConfigService(ConfigProperties properties) {
        return new DBConfigService(properties);
    }
}
