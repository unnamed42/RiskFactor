package com.tjh.riskfactor.controller;

import com.tjh.riskfactor.entity.User;
import com.tjh.riskfactor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('root', 'admin')")
public class GroupController {

    private final UserService service;

    @RequestMapping(value = "/{group}", method = RequestMethod.GET)
    Collection<String> groupMembers(@PathVariable String group) {
        return service.groupMembers(group);
    }

    @RequestMapping(value = "/{group}", method = RequestMethod.PUT)
    void appendMember(@PathVariable String group, @RequestBody List<String> users) {
        service.appendMembers(group, users);
    }

}
