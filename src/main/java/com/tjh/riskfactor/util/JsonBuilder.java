package com.tjh.riskfactor.util;

import lombok.val;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
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
        val writer = new StringWriter();
        val mapper = new ObjectMapper();
        try {
            mapper.writeValue(writer, data);
        } catch (IOException e) {
            // let our default exception handler handle it
            throw new UncheckedIOException(e);
        }
        return writer.toString();
    }

}
