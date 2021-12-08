package cn.edana.cement.dbconfig.sample;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SampleController {

    private final SampleProperties properties;

    @Value("${cement.username}")
    private String username;

    @GetMapping("/configs")
    public Map<String, Object> getConfigs() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("name", username);
        ret.put("age", properties.getAge());
        return ret;
    }
}
