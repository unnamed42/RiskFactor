package com.tjh.riskfactor.controllers;

import lombok.val;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.utils.HttpUtils;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @RequestMapping(method = RequestMethod.POST)
    void login(@RequestBody JsonNode body) {
        val username = HttpUtils.jsonNode(body, "username").asText();
        val password = HttpUtils.jsonNode(body, "password").asText();
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    void logout(@RequestBody JsonNode body) {
        val token = HttpUtils.jsonNode(body, "token").asText();
    }

}
