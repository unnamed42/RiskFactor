package com.tjh.riskfactor.utils;

import lombok.val;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.http.HttpStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.server.ResponseStatusException;

public class HttpUtils {

    public static <T, ID> T getById(CrudRepository<T, ID> repo, ID id) {
        val item = repo.findById(id);
        if(!item.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return item.get();
    }

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
