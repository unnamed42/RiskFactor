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

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    @PreAuthorize ("#username == principal.username")
    User getUser(@PathVariable String username) {
        return service.getUser(username);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
    @PreAuthorize ("#username == authentication.principal.username")
    void deleteUser(@PathVariable String username) {
        service.deleteUser(username);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.POST)
    @PreAuthorize ("#username == authentication.principal.username")
    void addUser(@PathVariable String username, @RequestBody NewUser json) {
        service.createUser(username, json);
    }

    @RequestMapping(value = "/{username}/password", method = RequestMethod.POST)
    @PreAuthorize ("#username == authentication.principal.username")
    void changePassword(@PathVariable String username, @RequestBody NewPassword body) {
        service.changePassword(username, body.getPassword());
    }

}
