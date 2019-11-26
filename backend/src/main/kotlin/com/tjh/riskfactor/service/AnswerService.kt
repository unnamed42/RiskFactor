package com.tjh.riskfactor.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.entity.form.*
import com.tjh.riskfactor.repo.AnswerEntryRepository
import com.tjh.riskfactor.repo.AnswerRepository

import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

@Service
class AnswerService(
    private val groups: GroupService,
    private val ansEntries: AnswerEntryRepository,
    override val repo: AnswerRepository
): IDBService<Answer>("answer") {

    /**
     * 获取回答的内容（不包含信息）
     * @param id 回答id
     * @return 回答的内容
     */
    fun answerBody(id: Int) = ansEntries.valueViewsOf(id).map {
        it.qid.toString() to it.value
    }.toMap()

    /**
     * 更新回答内容
     * @param id 回答id
     * @param body 有更新的内容
     */
    @Transactional
    fun updateAnswer(id: Int, body: Map<String, String>) {
        if(body.isEmpty())
            return
        body.entries.forEach{ (qid, value) ->
            ansEntries.putValue(id, qid.toInt(), value)
        }
    }

    @Transactional(readOnly = true)
    fun export(id: Int) {
        val answer = findChecked(id)
        val task = answer.task; val entries = answer.answers

    }

    fun answersOfTask(taskId: Int) = repo.findAllByTaskId(taskId)

    fun answersOfTask(taskId: Int, groupId: Int) = repo.findAllByTaskIdCreatedBy(taskId, groups.memberNames(groupId))

    fun answersCreatedBy(taskId: Int, userId: Int) = repo.findTaskAnswersCreatedBy(taskId, userId)

    internal fun saveEntries(entries: Iterable<AnswerEntry>) = ansEntries.saveAll(entries)
}
