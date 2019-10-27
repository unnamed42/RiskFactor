package com.tjh.riskfactor.controller;

import com.tjh.riskfactor.service.AccountService;
import com.tjh.riskfactor.service.DataService;
import com.tjh.riskfactor.service.FormService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
@PreAuthorize("@e.isRoot(principal)")
public class DebugController {

    private final DataService data;

    @GetMapping("/forms")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    void reloadQuestions() throws IOException {
        data.reloadForms();
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    void reloadUsers() throws IOException {
        data.reloadUsers();
    }

}
