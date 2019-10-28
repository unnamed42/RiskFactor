package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.service.AccountService;
import static com.tjh.riskfactor.error.ResponseErrors.invalidArg;

import java.util.Map;

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
    @PreAuthorize("@e.canManage(#username)")
    void deleteUser(@PathVariable String username) {
        service.deleteUser(username);
    }

    @PostMapping("/{username}")
    @PreAuthorize("@e.canManage(#username)")
    void addUser(@PathVariable String username, @RequestBody User user) {
        if(user.getUsername() == null)
            user.setUsername(username);
        service.createUser(user);
    }

    @PostMapping("/{username}/password")
    @PreAuthorize("@e.canManage(#username)")
    void changePassword(@PathVariable String username, @RequestBody Map<String, String> body) {
        val password = body.get("password");
        if(password == null)
            throw invalidArg("password", "null");
        service.changePassword(username, password);
    }

}
