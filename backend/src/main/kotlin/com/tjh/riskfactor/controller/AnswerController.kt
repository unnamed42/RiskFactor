package com.tjh.riskfactor.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.tjh.riskfactor.service.AnswerService;

@CrossOrigin
@RestController
class AnswerController {

    @Autowired private lateinit var service: AnswerService;

    /**
     * 获取回答的内容（不包含信息）
     * @param id 回答id
     * @return 回答的内容
     */
    @GetMapping("/answers/{id}/body")
    fun answer(@PathVariable id: Int) {
//        var ans = service.checkedFind(id);
//        var map = new HashMap<String, Map<String, Object>>();
////        for(var anssec : ans.getParts())
////            // TODO: fix
////            map.put(anssec.getId().toString(), anssec.getBody());
//        return map;
    }

    /**
     * 删除回答
     * @param id 回答id
     */
    @DeleteMapping("/answers/{id}")
    fun deleteAnswer(@PathVariable id: Int) = service.remove(id);

}
