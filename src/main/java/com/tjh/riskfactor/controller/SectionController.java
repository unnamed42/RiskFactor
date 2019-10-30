package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entity.form.Section;
import com.tjh.riskfactor.service.TaskService;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;

@RestController
@RequiredArgsConstructor
public class SectionController {

    private final TaskService service;

    @GetMapping("/section/{sid}")
    Section section(@PathVariable Integer sid) {
        return service.findSectionById(sid).orElseThrow(() -> notFound("section", sid.toString()));
    }

}
