package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.service.TaskService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @GetMapping("/task")
    List<?> availableTasks(Authentication auth) {
        return service.availableTasks(auth.getName());
    }

    @GetMapping("/task/{id}/sections")
    List<?> sectionNames(@PathVariable Integer id) {
        return service.taskSectionsInfo(id);
    }

}
