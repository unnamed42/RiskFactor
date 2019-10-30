package com.tjh.riskfactor.controller;


import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entity.form.Section;
import com.tjh.riskfactor.service.TaskService;

@RestController
@RequiredArgsConstructor
public class SectionController {

    private final TaskService service;

    @GetMapping("/section/{id}")
    Section section(@PathVariable Integer sid) {
        return service.findSectionById(sid);
    }

}
