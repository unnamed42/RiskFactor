package com.tjh.riskfactor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjh.riskfactor.entity.form.AnswerSection;
import com.tjh.riskfactor.entity.form.Section;
import lombok.val;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.service.AnswerService;

import com.tjh.riskfactor.entity.form.Answer;

import javax.servlet.http.HttpServletResponse;

import static com.tjh.riskfactor.error.ResponseErrors.notFound;
import static com.tjh.riskfactor.util.Utils.kvMap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
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

    @GetMapping(value = "/answer/{id}/file")
    HttpEntity<byte[]> answerFile(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        val value = answer(id);
        val mapper = new ObjectMapper();
        val headers = new HttpHeaders();
        val str = mapper.writeValueAsString(value);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.setHeader("Content-Disposition", "attachment; filename=export.json");
        return new HttpEntity<byte[]>(str.getBytes(), headers);
    }

//    @PostMapping("/answer/section/{sid}")
//    String saveAnswerSection(@PathVariable Integer sid, @RequestBody Map<String, Object> body) {
//        val ansId = service.saveAnswerSection(sid, body).getId();
//        return kvMap("id", ansId).buildJson().get();
//    }

}
