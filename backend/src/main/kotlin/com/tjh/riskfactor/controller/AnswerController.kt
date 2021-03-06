package com.tjh.riskfactor.controller

import com.fasterxml.jackson.databind.node.ObjectNode

import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal

import com.tjh.riskfactor.repository.IdType
import com.tjh.riskfactor.repository.propertyOf
import com.tjh.riskfactor.service.*

@CrossOrigin
@RestController
class AnswerController(
    private val service: AnswerService,
    private val accounts: AccountService
) {

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
        return service.answerInfoInSchema(schemaId, visible).asSequence()
            .filter { it.creator?.group != null }.map {
                val creator = it.creator!!
                AnswerInfo(id = it.id, creator = accounts.usernameOrId(creator.id),
                    group = accounts.findGroupName(creator.group!!.id),
                    createdAt = it.createdAt, modifiedAt = it.modifiedAt
                )
            }.toList()
    }

    /**
     *
     */
    @GetMapping("/answers/{answerId}/modifiedAt")
    fun answerModifiedAt(@PathVariable answerId: IdType) =
        service.answers.propertyOf(answerId) { modifiedAt }

    /**
     * 创建一个回答。目前认为所有有效用户都可以创建回答
     */
    @PostMapping("/answers")
    @PreAuthorize("@checker.isUserEnabled()")
    fun createAnswer(@RequestParam schemaId: IdType,
                     @RequestBody body: ObjectNode,
                     @AuthenticationPrincipal details: AccountDetails): Id {
        val answer = service.createAnswer(schemaId, details.dbUser, body)
        return Id(answer.id)
    }

    /**
     * 更新回答
     */
    @PutMapping("/answers/{answerId}")
    @PreAuthorize("@checker.isAnswerWritable(#answerId)")
    fun updateAnswer(@PathVariable answerId: IdType,
                     @RequestBody body: ObjectNode,
                     @AuthenticationPrincipal details: AccountDetails) {
        service.updateAnswer(answerId, body)
    }

    @DeleteMapping("/answers/{answerId}")
    @PreAuthorize("@checker.isAnswerWritable(#answerId)")
    fun removeAnswer(@PathVariable answerId: IdType) {
        service.removeAnswer(answerId)
    }
}

data class Id(val id: IdType)

data class AnswerInfo(
    val id: IdType,
    val creator: String,
    val group: String,
    val createdAt: Long,
    var modifiedAt: Long
)
