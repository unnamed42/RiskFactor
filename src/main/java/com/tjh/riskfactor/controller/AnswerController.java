package com.tjh.riskfactor.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import com.tjh.riskfactor.service.AnswerService;
import static com.tjh.riskfactor.error.ResponseErrors.notFound;

import java.util.HashMap;
import java.util.Map;

/**
 * 负责处理问题回答的Controller
 */
@RestController
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService service;

    /**
     * 获取回答的内容（不包含信息）
     * @param id 回答id
     * @return 回答的内容
     */
    @GetMapping("/answer/{id}/body")
    public Map<String, Map<String, Object>> answer(@PathVariable Integer id) {
        final var ans = service.answer(id).orElseThrow(() -> notFound("answer", id.toString()));
        final var map = new HashMap<String, Map<String, Object>>();
        for(final var anssec : ans.getParts())
            map.put(anssec.getSectionPath(), anssec.getBody());
        return map;
    }

    /**
     * 删除回答
     * @param id 回答id
     */
    @DeleteMapping("/answer/{id}")
    public void deleteAnswer(@PathVariable Integer id) {
        service.delete(id);
    }

//    @GetMapping(value = "/answer/{id}/file")
//    public HttpEntity<byte[]> answerFile(@PathVariable Integer id, HttpServletResponse response) throws IOException {
//        final var value = answer(id);
//        final var mapper = new ObjectMapper();
//        final var headers = new HttpHeaders();
//        final var str = mapper.writeValueAsString(value);
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        response.setHeader("Content-Disposition", "attachment; filename=export.json");
//        return new HttpEntity<>(str.getBytes(), headers);
//    }

//    @PostMapping("/answer/section/{sid}")
//    String saveAnswerSection(@PathVariable Integer sid, @RequestBody Map<String, Object> body) {
//        val ansId = service.saveAnswerSection(sid, body).getId();
//        return kvMap("id", ansId).buildJson().get();
//    }

}
