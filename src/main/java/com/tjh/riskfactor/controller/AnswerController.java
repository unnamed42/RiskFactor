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
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService service;

    /**
     * 获取回答的内容（不包含信息）
     * @param id 回答id
     * @return 回答的内容
     */
    @GetMapping("/answers/{id}/body")
    public Map<String, Map<String, Object>> answer(@PathVariable Integer id) {
        var ans = service.checkedFind(id);
        var map = new HashMap<String, Map<String, Object>>();
//        for(var anssec : ans.getParts())
//            // TODO: fix
//            map.put(anssec.getId().toString(), anssec.getBody());
        return map;
    }

    /**
     * 删除回答
     * @param id 回答id
     */
    @DeleteMapping("/answers/{id}")
    public void deleteAnswer(@PathVariable Integer id) {
        service.remove(id);
    }

//    @GetMapping(value = "/answer/{id}/file")
//    public HttpEntity<byte[]> answerFile(@PathVariable Integer id, HttpServletResponse response) throws IOException {
//        var value = answer(id);
//        var mapper = new ObjectMapper();
//        var headers = new HttpHeaders();
//        var str = mapper.writeValueAsString(value);
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
