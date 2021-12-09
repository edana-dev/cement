package com.maneki.cement.dbconfig.core;

public class DBConfigFactory {
    public static ConfigService createConfigService(DBConfigProperties properties) {
        return new DBConfigService(properties);
    }
}
