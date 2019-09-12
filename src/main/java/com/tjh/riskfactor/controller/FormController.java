package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entity.form.Section;
import com.tjh.riskfactor.service.FormService;

@RestController
@RequestMapping("/form")
@RequiredArgsConstructor
public class FormController {

    private final FormService service;

    @GetMapping("/section/{title}")
    Section getSection(@PathVariable String title) {
        return service.fetchFormSection(title);
    }

    @PostMapping("/section/{title}")
    void createSection(@PathVariable String title, @RequestBody Section section) {}

}
