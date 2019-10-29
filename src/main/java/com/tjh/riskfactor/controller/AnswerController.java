package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tjh.riskfactor.service.AnswerService;
import static com.tjh.riskfactor.repo.AnswerRepository.AnswerBrief;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService service;

    @GetMapping("/answer")
    List<AnswerBrief> readableAnswers(Authentication auth) {
        return service.readableAnswers(auth.getName());
    }

}
