package com.tjh.riskfactor.controller;

import com.tjh.riskfactor.entity.Role;
import lombok.val;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.util.HttpUtils;
import com.tjh.riskfactor.repo.UserRepository;
import com.tjh.riskfactor.repo.RoleRepository;
import com.tjh.riskfactor.json.AddUserRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;

    @Transactional(readOnly = true)
    User getUserOrThrow(String username) {
        return users.findByUsername(username).orElseThrow(() -> {
            val message = String.format("user [%s] not found", username);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        });
    }

    @Transactional(readOnly = true)
    Role getRoleOrThrow(String name) {
        return roles.findByName(name).orElseThrow(() -> {
            val message = String.format("invalid role name [%s]", name);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        });
    }

    private User toUser(String username, AddUserRequest json) {
        val roles = Arrays.stream(json.getRole().split(","))
                    .map(this::getRoleOrThrow).collect(Collectors.toList());
        val password = encoder.encode(json.getPassword());
        return new User().setUsername(username).setPassword(password)
                .setRoles(roles)
                .setEmail(json.getEmail());
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    List<User> getUsers() {
        return users.findAll();
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    User getUser(@PathVariable String username) {
        return getUserOrThrow(username);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    void deleteUser(@PathVariable String username) {
        users.deleteByUsername(username);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    void addUser(@PathVariable String username, @RequestBody AddUserRequest json) {
        users.save(toUser(username, json));
    }

    @RequestMapping(value = "/{username}/password", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    void changePassword(@PathVariable String username, @RequestBody JsonNode body) {
        String password = HttpUtils.jsonNode(body, "password").asText();
        password = encoder.encode(password);
        val user = getUserOrThrow(username).setPassword(password);
        users.save(user);
    }

}
