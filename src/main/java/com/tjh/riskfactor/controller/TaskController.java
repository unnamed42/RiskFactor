package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entity.form.Task;
import com.tjh.riskfactor.entity.form.Section;
import com.tjh.riskfactor.service.TaskService;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @GetMapping("/task")
    List<Task> availableTasks(Principal principal) {
        return service.availableTasks(principal.getName());
    }

    @GetMapping("/task/{id}/section-names")
    List<String> sectionNames(@PathVariable Integer id) {
        return service.taskSectionTitles(id);
    }

    @GetMapping("/task/{id}/sections")
    List<Section> sections(@PathVariable Integer id) {
        return service.taskSections(id);
    }

}
