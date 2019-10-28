package com.tjh.riskfactor.entity.cvt;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;
import javax.persistence.AttributeConverter;

@Slf4j
public class MapConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> source) {
        try {
            return mapper.writeValueAsString(source);
        } catch(JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String source) {
        try {
            val type = new TypeReference<Map<String, Object>>() {};
            return mapper.readValue(source, type);
        } catch(JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
