package com.maneki.cement.dbconfig.sample;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(SampleProperties.class)
@SpringBootApplication
public class DBConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(DBConfigApplication.class, args);
    }
}
