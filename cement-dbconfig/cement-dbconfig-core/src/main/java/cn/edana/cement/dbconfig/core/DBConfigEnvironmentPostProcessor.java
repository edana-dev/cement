//package cn.edana.cement.dbconfig.core;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.env.EnvironmentPostProcessor;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.springframework.core.env.MapPropertySource;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//public class DBConfigEnvironmentPostProcessor implements EnvironmentPostProcessor {
//    private static final String PROPERTY_SOURCE_NAME = "databaseProperties";
//
//    @Override
//    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
//        log.info("postProcessEnvironment");
//        Map<String, Object> source = new HashMap<>();
//        source.put("cement.username", "Sample");
//        source.put("cement.age", 18);
//        environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, source));
//    }
//}
