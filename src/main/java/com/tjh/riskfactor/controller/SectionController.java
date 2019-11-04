package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entity.form.Section;
import com.tjh.riskfactor.service.SectionService;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;

@RestController
@RequiredArgsConstructor
public class SectionController {

    private final SectionService service;

    @GetMapping("/sections/{sid}")
    Section section(@PathVariable Integer sid) {
        return service.section(sid).orElseThrow(() -> notFound("section", sid.toString()));
    }

}
