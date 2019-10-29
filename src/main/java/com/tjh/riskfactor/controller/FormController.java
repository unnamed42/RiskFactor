package com.tjh.riskfactor.controller;

import com.tjh.riskfactor.entity.form.Task;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.entity.form.Section;
import com.tjh.riskfactor.service.FormService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/form")
@RequiredArgsConstructor
public class FormController {

    private final FormService service;

    @GetMapping
    List<Task> availableTasks(Principal principal) {
        return service.availableTasks(principal.getName());
    }


}
