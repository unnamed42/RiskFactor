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
@PreAuthorize("hasAnyAuthority('root', 'admin')")
public class UserController {

    private final AccountService service;

    @GetMapping("/{username}")
    @PreAuthorize ("#username == principal.username")
    User getUser(@PathVariable String username) {
        return service.getUser(username);
    }

    @DeleteMapping("/{username}")
    @PreAuthorize ("#username == authentication.principal.username")
    void deleteUser(@PathVariable String username) {
        service.deleteUser(username);
    }

    @PostMapping("/{username}")
    @PreAuthorize ("#username == authentication.principal.username")
    void addUser(@PathVariable String username, @RequestBody NewUser json) {
        service.createUser(username, json);
    }

    @PostMapping("/{username}/password")
    @PreAuthorize ("#username == authentication.principal.username")
    void changePassword(@PathVariable String username, @RequestBody NewPassword body) {
        service.changePassword(username, body.getPassword());
    }

}
