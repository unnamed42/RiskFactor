package com.tjh.riskfactor.controller;

import com.tjh.riskfactor.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('root', 'admin')")
public class GroupController {

    private final AccountService service;

    @GetMapping
    Collection<String> listGroups() {
        return service.listGroups();
    }

    @GetMapping("/{group}")
    Collection<String> groupMembers(@PathVariable String group) {
        return service.listGroupMembers(group);
    }

    @PostMapping("/{group}")
    void createGroup(@PathVariable String group, @RequestBody(required = false) Collection<String> users) {
        service.createGroup(group, users);
    }

    @PutMapping("/{group}")
    void appendMember(@PathVariable String group, @RequestBody Collection<String> users) {
        service.addGroupMembers(group, users);
    }

    @DeleteMapping("/{group}")
    void deleteGroup(@PathVariable String group) {
        service.deleteGroup(group);
    }

}
