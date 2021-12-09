package com.maneki.cement.dbconfig.core;

import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DBConfigServiceTest {

    DBConfigService service;

    @Before
    public void setUp() throws Exception {
        DBConfigProperties properties = new DBConfigProperties();
        service = new DBConfigService(properties);
    }

    @Test
    public void testParseJson() {
        Object object = JSON.parse("[{\"string\": \"str\", \"integer\": 12, \"boolean\": true}, {}]");
        Map<String, Object> result = new HashMap<>();
        service.parseJson("", result, object);

        assertEquals("str", result.get("[0].string"));
        assertEquals(12, result.get("[0].integer"));
        assertEquals(true, result.get("[0].boolean"));
    }


    @Test
    public void testParseJson2() {
        Object object = JSON.parse("{\"string\": \"str\", \"integer\": 12, \"boolean\": true, \"array\": [1, 2, 3], \"object\": {\"string\": \"str\"} }");
        Map<String, Object> result = new HashMap<>();
        service.parseJson("", result, object);

        assertEquals("str", result.get("string"));
        assertEquals(12, result.get("integer"));
        assertEquals(true, result.get("boolean"));
        assertEquals(1, result.get("array.[0]"));
        assertEquals(2, result.get("array.[1]"));
        assertEquals(3, result.get("array.[2]"));
        assertEquals("str", result.get("object.string"));
    }

}