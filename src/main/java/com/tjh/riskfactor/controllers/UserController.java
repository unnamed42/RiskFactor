package com.tjh.riskfactor.controllers;

import lombok.val;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.tjh.riskfactor.entities.User;
import com.tjh.riskfactor.repos.UserRepository;
import com.tjh.riskfactor.utils.HttpUtils;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repo;

    private User getUserOrThrow(String username) {
        return repo.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("user [%s] not found", username)));
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    List<User> getUsers() {
        return repo.findAll();
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    User getUser(@PathVariable String username) {
        return getUserOrThrow(username);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    void deleteUser(@PathVariable String username) {
        repo.deleteById(username);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    void addUser(@PathVariable String username, @RequestBody JsonNode body) {
        val password = HttpUtils.jsonNode(body, "password").asText();
        val role = HttpUtils.jsonNode(body, "role").asText();
        val status = HttpUtils.jsonNode(body, "status").asText();
        val user = new User().setUsername(username).setPassword(password)
                        .setRole(role).setStatus(status);
        repo.save(user);
    }

    @RequestMapping(value = "/{username}/password", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    void changePassword(@PathVariable String username, @RequestBody JsonNode body) {
        val password = HttpUtils.jsonNode(body, "password").asText();
        val user = getUserOrThrow(username).setPassword(password);
        repo.save(user);
    }

}
