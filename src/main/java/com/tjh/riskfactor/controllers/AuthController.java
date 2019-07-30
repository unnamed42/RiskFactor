package com.tjh.riskfactor.controllers;

import lombok.val;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.tjh.riskfactor.utils.HttpUtils;
import com.tjh.riskfactor.repos.UserRepository;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository repo;

    private String getToken(String username, String password) {
        val user = repo.findById(username);
        val pass = user.map(value -> value.getPassword().equals(password))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("user [%s] not found", username)));
        if(!pass)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("user [%s] authentication failed", username));
        return "";
    }

    @RequestMapping(method = RequestMethod.POST)
    String login(@RequestBody JsonNode body) {
        val username = HttpUtils.jsonNode(body, "username").asText();
        val password = HttpUtils.jsonNode(body, "password").asText();
        return getToken(username, password);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    void logout(@RequestBody JsonNode body) {
        val token = HttpUtils.jsonNode(body, "token").asText();
    }

}
