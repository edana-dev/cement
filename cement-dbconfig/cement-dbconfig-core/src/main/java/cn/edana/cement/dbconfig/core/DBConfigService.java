package cn.edana.cement.dbconfig.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
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

@Slf4j
public class DBConfigService implements ConfigService {
    private DataSource dataSource;
    private String selectSQL;


    public DBConfigService(DBConfigProperties properties) {
        this.dataSource = DataSourceBuilder
                .create()
                .username(properties.getUsername())
                .password(properties.getPassword())
                .url(properties.getUrl())
                .driverClassName(properties.getDriverClassName())
                .build();
        this.selectSQL = properties.getSql();
    }

    @SneakyThrows
    @Override
    public Map<String, Object> getSource() {
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

    protected void parseJson(String prefix, Map<String, Object> result, Object object) {
        if (object == null) {
            return;
        }
//        if (object.getClass().isPrimitive()) {
//            result.put(prefix, object);
//            return;
//        }
        if (object instanceof String || object instanceof Boolean ||  Number.class.isAssignableFrom(object.getClass())) {
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
