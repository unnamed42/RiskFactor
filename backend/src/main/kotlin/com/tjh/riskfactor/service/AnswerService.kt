package com.tjh.riskfactor.service

import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import au.com.console.jpaspecificationdsl.*
import com.tjh.riskfactor.repository.*

@Service
class AnswerService(
    val answerValues: AnswerValueRepository,
    val answers: AnswerRepository,
    private val accounts: AccountService
) {

    /**
     * 获得回答的简略信息（即不包含回答的内容），用于列表展示
     * @param schemaId 获得此问卷对应的所有回答信息
     */
    fun answerInfoInSchema(schemaId: IdType): List<AnswerInfo> {
        // 初始值不能返回
        val query = Answer::schemaId.equal(schemaId) and Answer::isInitialData.isFalse()
        return answers.findAll(query).map { it.toInfo() }
    }

    fun answerInfoInSchema(schemaId: IdType, idList: Collection<IdType>): List<AnswerInfo> =
        answers.findAll(Answer::schemaId.equal(schemaId) and Answer::id.`in`(idList)).map { it.toInfo() }

    /**
     * 返回一个用户所能看见的全部回答。组管理员可看见所有组成员的回答，普通用户只能看见自己的，
     * 超级管理员可以看见所有
     */
    fun userVisibleAnswers(user: User): List<IdType> {
        val query: Specification<Answer> = when {
            user.isRoot -> `true`()
            user.isAdmin -> Answer::groupId.equal(user.groupId)
            else -> Answer::creatorId.equal(user.id)
        } and Answer::isInitialData.isFalse()
        val result: List<IdOnly> = answers.findAllProjected(query)
        return result.map { it.id }
    }

    /**
     * 返回一个扁平化的回答内容。其值为一个string -> string的map，
     * 其key代表回答对应问题的id，为将其利用为javascript的key，转换成"$${id}"的格式，即在前面加一个符号
     *   如果该问题是动态数量列表中的其中一个，则key的格式为"$${id}-${index}"，index为其所处位置，下标从1开始
     *     如上设计的话，动态数量列表这个问题本身提交的内容是它的长度
     * 其value代表回答的值
     */
    fun getAnswer(answerId: IdType): Map<String, String> =
        answerValues.findAll(AnswerValue::tableId.equal(answerId)).map {
            val identifier = "$${it.questionId}"
            if(it.order == 0)
                identifier to it.value
            else
                "$identifier-${it.order}" to it.value
        }.toMap()

    fun getInitialValues(schemaId: IdType): Map<String, String> {
        val answerId: IdOnly? = answers.findOneProjected(
            Answer::schemaId.equal(schemaId) and Answer::isInitialData.isTrue()
        )
        return if(answerId == null) mapOf() else getAnswer(answerId.id)
    }

    /**
     * 追加回答内容/创建新的回答。回答的创建者和所有组的信息跟随执行动作的用户。
     * @param answerId 追加的回答的id，更新原有的值。如果该值为0，则创建一个新的回答。
     *                 如果该id不存在，抛出异常而非创建新回答。
     * @param values 回答的内容，格式与[getAnswer]的返回值相同
     * @param actor 该动作的执行者
     */
    @Transactional
    fun writeAnswer(answerId: IdType, schemaId: IdType, values: Map<String, String>, actor: User) {
        val now = System.currentTimeMillis()
        // 获得Answer实体
        val answer = if(answerId == 0)
            answers.save(Answer(actor.id, actor.groupId, schemaId, now, now))
        else
            answers.updateUnchecked(answerId) { it.modifiedAt = now }
        // 将数据解构
        val data = values.map { (identifier, value) ->
            if(identifier.contains('-')) {
                val regex = "^\\$(\\d+)-(\\d+)$".toRegex()
                val (questionId, order) = regex.find(identifier)!!.destructured
                AnswerValue(answer.id, questionId.toInt(), order.toInt(), value)
            } else {
                val regex = "^\\$(\\d+)$".toRegex()
                val (questionId) = regex.find(identifier)!!.destructured
                AnswerValue(answer.id, questionId.toInt(), 0, value)
            }
        }
        answerValues.saveAll(data)
    }

    private fun Answer.toInfo() = AnswerInfo(
        id = id, creator = accounts.usernameOrId(creatorId),
        createdAt = createdAt, modifiedAt = modifiedAt,
        group = accounts.findGroupName(groupId)
    )

}

data class AnswerInfo(
    val id: IdType,
    val creator: String,
    val group: String,
    val createdAt: Long,
    var modifiedAt: Long
)
