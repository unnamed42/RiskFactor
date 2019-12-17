package com.tjh.riskfactor.service

import com.tjh.riskfactor.entity.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.entity.form.*
import com.tjh.riskfactor.repo.AnswerRepository

import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

@Service
class AnswerService(
    private val groups: GroupService,
    private val tasks: TaskService,
    override val repo: AnswerRepository
): IDBService<Answer>("answer") {

    /**
     * 获取回答的内容（不包含信息）
     * @param id 回答id
     * @return 回答的内容
     */
    @Transactional
    fun answerBody(id: Int) = repo.bodyOf(id) ?: "undefined"

    /**
     * 创建或修改的回答。返回的格式为：
     * ```
     * {
     *      "id": [回答的id]
     * }
     * ```
     * @param answerId 为`null`时代表是要创建一个新的[Answer]，否则是修改id为[answerId]的[Answer]
     * @param taskId 项目id
     * @param creator 创建者（用户）实体
     * @param body 回答内容，应该是合法的JSON内容
     * @return 新创建的回答id
     */
    @Transactional
    fun postAnswer(answerId: Int?, taskId: Int, creator: User, body: String): Map<String, Int> {
        val answer = if(answerId == null)
            Answer(creator = creator, task = tasks.findChecked(taskId)).apply { this.body = body }
        else
            updateChecked(answerId) { it.body = body }
        return mapOf("id" to save(answer).id)
    }

    fun answersOfTask(taskId: Int) = repo.findAllByTaskId(taskId)

    fun answersOfTask(taskId: Int, groupId: Int) =
        repo.findAllByTaskIdCreatedBy(taskId, groups.repo.findMemberNames(groupId))

    fun answersCreatedBy(taskId: Int, userId: Int) = repo.findTaskAnswersCreatedBy(taskId, userId)

}
