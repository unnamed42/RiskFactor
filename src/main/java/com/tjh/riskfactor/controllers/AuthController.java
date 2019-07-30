package com.tjh.riskfactor.controllers;

import lombok.val;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.utils.HttpUtils;
import com.tjh.riskfactor.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @RequestMapping(method = RequestMethod.POST)
    String login(@RequestBody JsonNode body) {
        val username = HttpUtils.jsonNode(body, "username").asText();
        val password = HttpUtils.jsonNode(body, "password").asText();
        return service.getToken(username, password);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    void logout(@RequestBody JsonNode body) {
        val token = HttpUtils.jsonNode(body, "token").asText();
    }

}
