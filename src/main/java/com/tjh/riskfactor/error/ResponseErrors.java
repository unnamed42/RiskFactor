package com.tjh.riskfactor.error;

import lombok.val;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResponseErrors {
    public static ResponseStatusException notFound(String field, String ...names) {
        val message = String.format("requested %s(s) [%s] not found", field, String.join(",", names));
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    public static ResponseStatusException conflict(String field, String name) {
        val message = String.format("%s [%s] already exists", field, name);
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }

    public static ResponseStatusException invalidArg(String field, String value) {
        val message = String.format("argument [%s] has invalid value [%s]", field, value);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
