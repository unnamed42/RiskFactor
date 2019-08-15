package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.util.HttpUtils;
import com.tjh.riskfactor.json.AddUserRequest;
import com.tjh.riskfactor.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService users;

    @RequestMapping(value = "", method = RequestMethod.GET)
    List<User> getUsers() {
        return users.getAll();
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    User getUser(@PathVariable String username) {
        return users.getUser(username);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
    void deleteUser(@PathVariable String username) {
        users.deleteUser(username);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.POST)
    void addUser(@PathVariable String username, @RequestBody AddUserRequest json) {
        users.createUser(username, json);
    }

    @RequestMapping(value = "/{username}/password", method = RequestMethod.POST)
    void changePassword(@PathVariable String username, @RequestBody JsonNode body) {
        String password = HttpUtils.jsonNode(body, "password").asText();
        users.changePassword(username, password);
    }

}
