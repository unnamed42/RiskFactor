package com.tjh.riskfactor.service;

import lombok.val;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class Errors {
    static ResponseStatusException notFound(String field, String ...names) {
        val message = String.format("requested %s(s) [%s] not found", field, String.join(",", names));
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    static ResponseStatusException conflict(String field, String name) {
        val message = String.format("%s [%s] already exists", field, name);
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}
