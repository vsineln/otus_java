package ru.otus.homework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "feclients")
public class FeClientsProperties {

    /**
     * key is name of the client, value - its host anp port separated by ":"
     * Example: "frontend1: localhost:9009"
     */
    private final Map<String, String> map = new HashMap<>();

    public Map<String, String> getMap() {
        return map;
    }
}
