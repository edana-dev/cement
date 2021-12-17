package com.maneki.cement.config.core;

import javax.sql.DataSource;
import java.util.Map;

public interface ConfigService {
    /**
     * 获取配置项
     *
     * @return 返回配置项
     */
    Map<String, Object> getSource();

    void addListener(ConfigListener listener);

    void removeListener(ConfigListener listener);

    DataSource getDataSource();
}
