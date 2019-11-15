package com.tjh.riskfactor.controller;

import org.springframework.web.bind.annotation.*

import com.tjh.riskfactor.service.AnswerService

@CrossOrigin
@RestController
class AnswerController(private val service: AnswerService) {

    @GetMapping("/answers/{id}/body")
    fun answer(@PathVariable id: Int) = service.answerBody(id)

    @GetMapping("/answers/{id}")
    fun exportAnswer(@PathVariable id: Int) = service.export(id)

    @PutMapping("/answers/{id}/body")
    fun updateAnswer(@PathVariable id: Int, @RequestBody body: Map<String, String>) = service.updateAnswer(id, body)

    /**
     * 删除回答
     * @param id 回答id
     */
    @DeleteMapping("/answers/{id}")
    fun deleteAnswer(@PathVariable id: Int) = service.remove(id);

}
