package com.tjh.riskfactor.controller;

import lombok.val;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.repo.UserRepository;
import com.tjh.riskfactor.util.HttpUtils;
import com.tjh.riskfactor.json.AddUserRequest;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    private User getUserOrThrow(String username) {
        return repo.findByUsername(username)
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
        repo.deleteByUsername(username);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    void addUser(@PathVariable String username, @RequestBody AddUserRequest json) {
        val password = encoder.encode(json.getPassword());
        repo.save(json.toUser(username).setPassword(password));
    }

    @RequestMapping(value = "/{username}/password", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    void changePassword(@PathVariable String username, @RequestBody JsonNode body) {
        val password = HttpUtils.jsonNode(body, "password").asText();
        val user = getUserOrThrow(username).setPassword(password);
        repo.save(user);
    }

}
