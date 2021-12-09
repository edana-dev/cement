package cn.edana.cement.dbconfig.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DBConfigService implements ConfigService {

    public static final long DEFAULT_CACHE_MILLS = 10_000L;

    private DataSource dataSource;
    private String selectSQL;

    private final ScheduledExecutorService executor;

    private Map<String, Object> cacheSource;
    private Long cacheTimestamp = 0L;

    private List<ConfigListener> listeners = new ArrayList<>();


    public DBConfigService(DBConfigProperties properties) {
        this.dataSource = DataSourceBuilder
                .create()
                .username(properties.getUsername())
                .password(properties.getPassword())
                .url(properties.getUrl())
                .driverClassName(properties.getDriverClassName())
                .build();
        this.selectSQL = properties.getSql();
        this.executor = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName(this.getClass().getName());
                t.setDaemon(true);
                return t;
            }
        });

        this.executor.scheduleWithFixedDelay(() -> {
            try {
                DBConfigService.this.checkConfigInfo();
            } catch (Throwable e) {
                log.error("db config rotate check error", e);
            }
        }, 1L, 10L, TimeUnit.SECONDS);
    }

    public void checkConfigInfo() {
        Map<String, Object> source = doGetSource();
        if (!CollectionUtils.isEmpty(source) && !source.equals(cacheSource)) {
            this.listeners.forEach(ConfigListener::onConfigChanged);
            cacheSource(source);
            log.info("Refresh DB Config");
        }

    }

    @SneakyThrows
    @Override
    public Map<String, Object> getSource() {
        if (this.cacheSource != null && System.currentTimeMillis() - cacheTimestamp < DEFAULT_CACHE_MILLS) {
            return this.cacheSource;
        }

        Map<String, Object> source = doGetSource();
        cacheSource(source);
        return source;
    }

    private void cacheSource(Map<String, Object> source) {
        this.cacheSource = source;
        this.cacheTimestamp = System.currentTimeMillis();
    }

    private Map<String, Object> doGetSource() {
        List<PropertyItem> items = getPropertyItems();
        Map<String, Object> result = new HashMap<>();
        for (PropertyItem item : items) {
            switch (item.getType()) {
                case "json":
                    parseJson("", result, JSON.parse(item.getValue()));
                    break;
                case "string":
                    result.put(item.getName(), item.getValue());
                    break;
                case "int":
                    result.put(item.getName(), Integer.valueOf(item.getValue()));
                    break;
                case "boolean":
                    result.put(item.getName(), Boolean.valueOf(item.getValue()));
                    break;
                default:
                    log.warn("unsupported property type, type={}, name={}", item.getType(), item.getName());
            }
        }
        return result;
    }

    @Override
    public void addListener(ConfigListener listener) {
        if (this.listeners.contains(listener)) {
            return;
        }
        synchronized (this) {
            if (this.listeners.contains(listener)) {
                return;
            }
            this.listeners.add(listener);
        }
    }

    @Override
    public void removeListener(ConfigListener listener) {
        synchronized (this) {
            this.listeners.remove(listener);
        }
    }

    protected void parseJson(String prefix, Map<String, Object> result, Object object) {
        if (object == null) {
            return;
        }
//        if (object.getClass().isPrimitive()) {
//            result.put(prefix, object);
//            return;
//        }
        if (object instanceof String || object instanceof Boolean || Number.class.isAssignableFrom(object.getClass())) {
            result.put(prefix, object);
            return;
        }
        if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) object;
            for (int i = 0; i < jsonArray.size(); i++) {
                parseJson(newPrefix(prefix, String.format("[%d]", i)), result, jsonArray.get(i));
            }
        }
        if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            jsonObject.keySet().forEach(key ->
                    parseJson(newPrefix(prefix, key), result, jsonObject.get(key)));
        }
    }

    private String newPrefix(String prefix, String key) {
        if (StringUtils.isEmpty(prefix)) {
            return key;
        }
        return String.format("%s.%s", prefix, key);
    }

    protected List<PropertyItem> getPropertyItems() {
        log.debug("Fetching property items from database...");
        List<PropertyItem> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = this.dataSource.getConnection();
            statement = connection.prepareStatement(selectSQL);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                PropertyItem item = new PropertyItem();
                item.setName(resultSet.getString("name"));
                item.setType(resultSet.getString("type"));
                item.setValue(resultSet.getString("value"));
                result.add(item);
            }
        } catch (SQLException e) {
            throw new DBConfigException("执行SQL失败", e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new DBConfigException("SQL资源关闭失败", e);
            }
        }
        return result;
    }


}
