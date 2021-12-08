package cn.edana.cement.dbconfig.core;

import java.util.Map;

public interface ConfigService {
    /**
     * 获取配置项
     *
     * @return
     */
    Map<String, Object> getSource();

    void addListener(ConfigListener listener);

    void removeListener(ConfigListener listener);

}
