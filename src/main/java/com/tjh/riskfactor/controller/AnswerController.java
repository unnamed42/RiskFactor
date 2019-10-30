package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import lombok.val;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import com.tjh.riskfactor.service.AnswerService;
import static com.tjh.riskfactor.util.Utils.want;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService service;

    @GetMapping("/answer")
    List<?> writableAnswers(Authentication auth) {
        return service.writableAnswers(auth.getName());
    }

    @PostMapping("/answer")
    void submit(@RequestBody Map<String, Object> node) {
        val task = want(node, "task", String.class);
    }

}
