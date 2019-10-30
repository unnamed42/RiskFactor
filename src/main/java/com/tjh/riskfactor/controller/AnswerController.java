package com.tjh.riskfactor.controller;

import com.tjh.riskfactor.entity.form.AnswerSection;
import com.tjh.riskfactor.entity.form.Section;
import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.service.AnswerService;

import com.tjh.riskfactor.entity.form.Answer;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;
import static com.tjh.riskfactor.util.Utils.kvMap;

import java.util.HashMap;
import java.util.Map;
import static java.util.stream.Collectors.toMap;

@RestController
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService service;

    @GetMapping("/answer/{id}")
    Map<String, Map<String, Object>> answer(@PathVariable Integer id) {
        val ans = service.answer(id).orElseThrow(() -> notFound("answer", id.toString()));
        val map = new HashMap<String, Map<String, Object>>();
        for(val anssec : ans.getParts())
            map.put(anssec.getSectionPath(), anssec.getBody());
        return map;
    }

//    @PostMapping("/answer/section/{sid}")
//    String saveAnswerSection(@PathVariable Integer sid, @RequestBody Map<String, Object> body) {
//        val ansId = service.saveAnswerSection(sid, body).getId();
//        return kvMap("id", ansId).buildJson().get();
//    }

}
