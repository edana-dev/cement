package cn.edana.cement.dbconfig.core;

import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

// After Nacos Locator
@Order(2)
public class DBConfigPropertySourceLocator implements PropertySourceLocator {

    @Override
    public PropertySource<?> locate(Environment environment) {
        Map<String, Object> source = new HashMap<>();
        source.put("cement.username", "Sample");
        source.put("cement.age", 20);
        return new MapPropertySource("dbconfig", source);
    }
}
