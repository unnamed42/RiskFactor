package com.tjh.riskfactor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjh.riskfactor.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.tjh.riskfactor.error.ResponseErrors.notFound;

@RestController
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService service;

    @GetMapping("/answer/{id}")
    Map<String, Map<String, Object>> answer(@PathVariable Integer id) {
        final var ans = service.answer(id).orElseThrow(() -> notFound("answer", id.toString()));
        final var map = new HashMap<String, Map<String, Object>>();
        for(final var anssec : ans.getParts())
            map.put(anssec.getSectionPath(), anssec.getBody());
        return map;
    }

    @GetMapping(value = "/answer/{id}/file")
    HttpEntity<byte[]> answerFile(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        final var value = answer(id);
        final var mapper = new ObjectMapper();
        final var headers = new HttpHeaders();
        final var str = mapper.writeValueAsString(value);
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
