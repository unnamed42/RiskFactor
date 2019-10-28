package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entity.form.Section;
import com.tjh.riskfactor.entity.form.Sections;
import com.tjh.riskfactor.service.FormService;

import java.util.List;

@RestController
@RequestMapping("/form")
@RequiredArgsConstructor
public class FormController {

    private final FormService service;

    @GetMapping("/sections")
    List<Sections> getSections() {
        return service.sections();
    }

    @GetMapping("/sections/{name}")
    Sections getSectionsByName(@PathVariable String name) {
        return service.sectionsByName(name);
    }

    @GetMapping("/section/{title}")
    Section getSection(@PathVariable String title) {
        return service.section(title);
    }

    @PostMapping("/section/{title}")
    void createSection(@PathVariable String title, @RequestBody Section section) {
        service.saveSection(section, title);
    }

}
