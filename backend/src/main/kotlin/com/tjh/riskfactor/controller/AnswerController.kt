package com.tjh.riskfactor.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

import com.tjh.riskfactor.repository.IdType
import com.tjh.riskfactor.service.AccountDetails
import com.tjh.riskfactor.service.AnswerInfo
import com.tjh.riskfactor.service.AnswerService

@CrossOrigin
@RestController
class AnswerController(private val service: AnswerService) {

    /**
     * 获得回答
     */
    @GetMapping("/answers/{answerId}")
    @PreAuthorize("@checker.isAnswerReadable(#answerId)")
    fun getAnswer(@PathVariable answerId: IdType) =
        service.getAnswer(answerId)

    /**
     * 获得问卷的回答信息列表
     */
    @GetMapping("/answers")
    fun getAnswerInfoInSchema(@RequestParam schemaId: IdType,
                              @AuthenticationPrincipal details: AccountDetails): List<AnswerInfo> {
        val visible = service.userVisibleAnswers(details.dbUser)
        return service.answerInfoInSchema(schemaId, visible)
    }

    /**
     * 写入回答，[answerId]为0时创建回答。需要[schemaId]是为了创建时填入[Answer.schemaId]
     */
    @PostMapping("/answers/{answerId}")
    @PreAuthorize("#answerId == 0 || @checker.isAnswerWritable(#answerId)")
    fun updateAnswer(@PathVariable answerId: IdType,
                     @RequestParam schemaId: IdType,
                     @RequestBody body: Map<String, String>,
                     @AuthenticationPrincipal details: AccountDetails) {
        service.writeAnswer(answerId, schemaId, body, details.dbUser)
    }
}
