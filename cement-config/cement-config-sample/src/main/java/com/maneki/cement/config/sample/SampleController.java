package com.maneki.cement.config.sample;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RefreshScope
@RestController
@RequiredArgsConstructor
public class SampleController implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final SampleProperties properties;

    @GetMapping("/configs")
    public Map<String, Object> getConfigs() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("name", properties.getUsername());
        ret.put("age", properties.getAge());
        return ret;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
