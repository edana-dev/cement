package cn.edana.cement.dbconfig.boot;

import cn.edana.cement.dbconfig.core.DBConfigPropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBConfigAutoConfiguration {
    @Bean
    public DBConfigPropertySourceLocator samplePropertySourceLocator() {
        return new DBConfigPropertySourceLocator();
    }
}
