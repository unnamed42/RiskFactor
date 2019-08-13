package com.tjh.riskfactor.util;

import lombok.val;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class HttpUtils {

    public static JsonNode jsonNode(JsonNode json, String fieldName) {
        return jsonNode(json, fieldName, false);
    }

    public static JsonNode jsonNode(JsonNode json, String fieldName, boolean nullable) {
        val node = json.get(fieldName);
        if(node == null || (!nullable && node.isNull()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("no valid field [%s] in request", fieldName));
        return node;
    }

}
