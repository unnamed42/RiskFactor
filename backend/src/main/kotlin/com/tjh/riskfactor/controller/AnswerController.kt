package com.tjh.riskfactor.controller;

import org.springframework.web.bind.annotation.*

import com.tjh.riskfactor.repo.*

@CrossOrigin
@RestController
class AnswerController(private val repo: AnswerRepository) {

    @GetMapping("/answers/{id}/body")
    fun answer(@PathVariable id: Int) = repo.bodyOf(id) ?: "undefined"

    /**
     * 删除回答
     * @param id 回答id
     */
    @DeleteMapping("/answers/{id}")
    fun deleteAnswer(@PathVariable id: Int) = repo.deleteById(id)

}
