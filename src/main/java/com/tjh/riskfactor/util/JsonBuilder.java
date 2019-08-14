package com.tjh.riskfactor.util;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.HashMap;

public class JsonBuilder {

    private final Map<String, Object> data;

    public JsonBuilder() {
        data = new HashMap<>();
    }

    public JsonBuilder add(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public String build() {
        try {
            return new ObjectMapper().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
