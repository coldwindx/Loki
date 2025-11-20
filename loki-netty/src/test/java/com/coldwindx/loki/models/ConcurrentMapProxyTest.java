package com.coldwindx.loki.models;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class ConcurrentMapProxyTest {
    @AllArgsConstructor
    class Package{
        private int id;
        private String name;
    }
    @Test
    public void testConcurrentMapProxy() {
        Map<String, Package> map = new ConcurrentMapProxy<>("test", "test.csv");
        map.put("key1", new Package(1, "value1"));
        map.put("key2", new Package(2, "value2"));
        map.remove("key1");
    }
}