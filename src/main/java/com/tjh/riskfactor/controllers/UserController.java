package com.tjh.riskfactor.controllers;

import lombok.val;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entities.User;
import com.tjh.riskfactor.services.UserService;
import com.tjh.riskfactor.utils.HttpUtils;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @RequestMapping(value = "", method = RequestMethod.GET)
    List<User> getUsers() {
        return service.getUsers();
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    User getUser(@PathVariable String username) {
        return service.getUser(username);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    void deleteUser(@PathVariable String username) {
        service.deleteUser(username);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    void addUser(@PathVariable String username, @RequestBody JsonNode body) {
        val password = HttpUtils.jsonNode(body, "password").asText();
        val role = HttpUtils.jsonNode(body, "role").asText();
        val status = HttpUtils.jsonNode(body, "status").asText();
        val user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        user.setStatus(status);
        service.addUser(user);
    }

    @RequestMapping(value = "/{username}/password", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    void changePassword(@PathVariable String username, @RequestBody JsonNode body) {
        val password = HttpUtils.jsonNode(body, "password").asText();
        service.changePassword(username, password);
    }

}
