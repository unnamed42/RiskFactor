package com.tjh.riskfactor.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.tjh.riskfactor.service.AnswerService;

@CrossOrigin
@RestController
class AnswerController {

    @Autowired private lateinit var service: AnswerService;

    @GetMapping("/answers/{id}/body")
    fun answer(@PathVariable id: Int) = service.answerBody(id)

    @PutMapping("/answers/{id}/body")
    fun updateAnswer(@PathVariable id: Int, @RequestBody body: Map<String, String>) = service.updateAnswer(id, body)

    /**
     * 删除回答
     * @param id 回答id
     */
    @DeleteMapping("/answers/{id}")
    fun deleteAnswer(@PathVariable id: Int) = service.remove(id);

}
