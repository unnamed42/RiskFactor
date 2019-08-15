package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.util.HttpUtils;
import com.tjh.riskfactor.json.AddUserRequest;
import com.tjh.riskfactor.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('root', 'admin')")
public class UsersController {

    private final UserService service;

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    User getUser(@PathVariable String username) {
        return service.getUser(username);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
    void deleteUser(@PathVariable String username) {
        service.deleteUser(username);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.POST)
    void addUser(@PathVariable String username, @RequestBody AddUserRequest json) {
        service.createUser(username, json);
    }

    @RequestMapping(value = "/{username}/password", method = RequestMethod.POST)
    void changePassword(@PathVariable String username, @RequestBody JsonNode body) {
        String password = HttpUtils.jsonNode(body, "password").asText();
        service.changePassword(username, password);
    }

}
