package com.tjh.riskfactor.controller;

import org.springframework.web.bind.annotation.*

import com.tjh.riskfactor.service.AnswerService

@CrossOrigin
@RestController
class AnswerController(private val service: AnswerService) {

    @GetMapping("/answers/{id}/body")
    fun answer(@PathVariable id: Int) = service.answerBody(id)

    /**
     * 删除回答
     * @param id 回答id
     */
    @DeleteMapping("/answers/{id}")
    fun deleteAnswer(@PathVariable id: Int) = service.remove(id);

}
