package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tjh.riskfactor.service.DataService;

import java.io.IOException;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@PreAuthorize("@e.isRoot()")
public class DebugController {

    private final DataService data;

    @GetMapping("/debug/tasks")
    @ResponseStatus(HttpStatus.OK)
    public void reloadQuestions() throws IOException {
        data.reloadTask();
    }

    @GetMapping("/debug/users")
    @ResponseStatus(HttpStatus.OK)
    public void reloadUsers() throws IOException {
        data.reloadUsers();
    }

}
