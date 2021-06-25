package com.tjh.riskfactor.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.util.RawValue

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import au.com.console.jpaspecificationdsl.*

import com.tjh.riskfactor.repository.*

@Service
class AnswerService(
    val answerValues: AnswerValueRepository,
    val answers: AnswerRepository,
    private val accounts: AccountService,
    private val mapper: ObjectMapper
) {

    /**
     * 获得回答的简略信息（即不包含回答的内容），用于列表展示
     * @param schemaId 获得此问卷对应的所有回答信息
     */
//    fun answerInfoInSchema(schemaId: IdType): List<Answer> =
//        // 初始值不能返回
//        answers.findAll(Answer::schemaId.equal(schemaId) and Answer::creatorId.notEqual(0))

    fun answerInfoInSchema(schemaId: IdType, idList: Collection<IdType>): List<Answer> =
        answers.findAll(Answer::schemaId.equal(schemaId) and Answer::id.`in`(idList))

    /**
     * 返回一个用户所能看见的全部回答。组管理员可看见所有组成员的回答，普通用户只能看见自己的，
     * 超级管理员可以看见所有
     */
    fun userVisibleAnswers(user: User): List<IdType> {
        val visible = accounts.visibleUserIds(user.id)
        return answers.findIdsWhenCreatorIn(visible)
    }

    private fun findChildren(rootObjectId: IdType, ordered: Boolean = false): List<AnswerValue> {
        val query = AnswerValue::parentId.equal(rootObjectId)
        return if(ordered) answerValues.findAll(query, AnswerValue::order.sorted()) else answerValues.findAll(query)
    }

    private fun AnswerValue.toJsonNode(): JsonNode = when(type) {
        AnswerType.OBJECT -> {
            val obj = mapper.createObjectNode()
            findChildren(id).forEach { obj.set<JsonNode>(it.question, it.toJsonNode()) }
            obj
        }
        AnswerType.ARRAY -> {
            val arr = mapper.createArrayNode()
            findChildren(id, true).forEach { arr.add(it.toJsonNode()) }
            arr
        }
        AnswerType.STRING -> mapper.nodeFactory.textNode(value)
        else -> mapper.nodeFactory.rawValueNode(RawValue(value))
    }

    @Transactional(readOnly = true)
    fun getAnswer(answerId: IdType): ObjectNode =
        answerValues.find(answers.propertyOf(answerId) { rootObjectId }).toJsonNode() as ObjectNode

    private fun JsonNode.toAnswerValue(question: String?, parentId: IdType?): AnswerValue {
        val entity = AnswerValue(question = question, parentId = parentId)
        when {
            isObject -> entity.type = AnswerType.OBJECT
            isArray -> entity.type = AnswerType.ARRAY
            else -> {
                entity.value = this.asText()
                when {
                    isTextual -> entity.type = AnswerType.STRING
                    isBoolean -> entity.type = AnswerType.BOOLEAN
                    else -> entity.type = AnswerType.NUMBER
                }
            }
        }
        return entity
    }

    /**
     * 将JSON分解为单个的条目存入数据库。该函数只考虑全新创建的情况
     */
    private fun JsonNode.persist(name: String?, parentId: IdType?, order: Int? = null): AnswerValue {
        val entity = answerValues.save(this.toAnswerValue(name, parentId))
        when {
            isObject -> (this as ObjectNode).fields().forEach { (key, node) -> node.persist(key, entity.id) }
            isArray -> (this as ArrayNode).forEachIndexed { index, node -> node.persist(null, entity.id, index) }
        }
        return entity
    }

    /**
     * 创建新的回答。
     * @param schemaId 回答所对应的问卷
     * @param actor 创建回答的用户
     * @param body 回答的内容，JSON格式
     */
    @Transactional
    fun createAnswer(schemaId: IdType, actor: User, body: ObjectNode): Answer {
        val answer = Answer(creator = actor, schemaId = schemaId,
            rootObjectId = body.persist(name = null, parentId = null).id)
        return answers.save(answer)
    }

    fun updateNode(oldNodeId: IdType, body: ObjectNode) {
        val children = findChildren(oldNodeId).map { it.question to it }.toMap()
        body.fields().forEach { (key, node) ->
            val childNode = children[key]
            if(childNode == null)
                node.persist(key, oldNodeId)
            else when {
                node.isObject -> updateNode(childNode.id, node as ObjectNode)
                // 更新数组时，整个删除再重新存入。希望有更好的方式
                node.isArray -> { removeNode(childNode.id); node.persist(key, oldNodeId) }
                node.isNull -> removeNode(childNode.id)
                else -> answerValues.save(childNode.apply { value = node.asText() })
            }
        }
    }

    /**
     * 更新回答。如果[body]中某个项的值为`null`，那么删除该项。
     */
    @Transactional
    fun updateAnswer(answerId: IdType, body: ObjectNode) {
        val now = System.currentTimeMillis()
        val rootObjectId = answers.propertyOf(answerId) { rootObjectId }
        updateNode(rootObjectId, body)
        answers.update(answerId) { modifiedAt = now }
    }

    /**
     * 删除回答的JSON结构中，根节点id为[rootId]的JSON node
     */
    private fun removeNode(rootId: IdType) {
        val remove = mutableListOf<AnswerValue>()
        var traverse = listOf(answerValues.find(rootId))
        while(traverse.isNotEmpty()) {
            val nextTraverse = mutableListOf<AnswerValue>()
            traverse.forEach {
                remove.add(it)
                if(it.type == AnswerType.ARRAY || it.type == AnswerType.OBJECT)
                    nextTraverse.addAll(findChildren(it.id))
            }
            traverse = nextTraverse
        }
        answerValues.deleteAllInBatch(remove)
    }

    @Transactional
    fun removeAnswer(answerId: IdType) {
        val rootId = answers.propertyOf(answerId) { rootObjectId }
        removeNode(rootId)
        answers.deleteById(answerId)
    }

}
