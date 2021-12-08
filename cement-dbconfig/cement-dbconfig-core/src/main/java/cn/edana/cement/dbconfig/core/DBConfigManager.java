package cn.edana.cement.dbconfig.core;

import lombok.Getter;

import java.util.Objects;

public class DBConfigManager {

    @Getter
    private final DBConfigProperties properties;

    private static ConfigService service;

    public DBConfigManager(DBConfigProperties properties) {
        this.properties = properties;
        createConfigService(properties);
    }

    /**
     * Compatible with old design,It will be perfected in the future.
     */
    static ConfigService createConfigService(
            DBConfigProperties properties) {
        if (Objects.isNull(service)) {
            synchronized (DBConfigManager.class) {
                if (Objects.isNull(service)) {
                    service = DBConfigFactory.createConfigService(properties);
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
