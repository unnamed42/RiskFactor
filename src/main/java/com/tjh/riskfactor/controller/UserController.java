package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.entity.json.NewUser;
import com.tjh.riskfactor.entity.json.NewPassword;
import com.tjh.riskfactor.service.AccountService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final AccountService service;

    @GetMapping("/{username}")
    User getUser(@PathVariable String username) {
        return service.getUser(username);
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("#username == principal.username or @e.isRoot(principal)")
    void deleteUser(@PathVariable String username) {
        service.deleteUser(username);
    }

    @PostMapping("/{username}")
    @PreAuthorize("@e.isRoot(principal)")
    void addUser(@PathVariable String username, @RequestBody NewUser json) {
        service.createUser(username, json);
    }

    @PostMapping("/{username}/password")
    @PreAuthorize("#username == principal.username or @e.isRoot(principal)")
    void changePassword(@PathVariable String username, @RequestBody NewPassword body) {
        service.changePassword(username, body.getPassword());
    }

}
