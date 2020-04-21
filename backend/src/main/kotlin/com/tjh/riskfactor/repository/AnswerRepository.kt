package com.tjh.riskfactor.repository

import com.fasterxml.jackson.annotation.JsonValue

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.Modifying

import javax.persistence.*

/**
 * 存储的Answer，json结构分解后的对象
 */
@Entity @Table
class AnswerValue(
    @Enumerated
    var type: AnswerType = AnswerType.OBJECT,

    /**
     * 内容。除[AnswerType.ARRAY]和[AnswerType.OBJECT]外都不为null
     */
    var value: String? = null,

    /**
     * 对应问题的name path。若[type]为[AnswerType.ARRAY]，则可为null。
     */
    var question: String? = null,

    /**
     * 在json结构中所属的直接上级node的id
     */
    var parentId: IdType? = null,

    /**
     * 所对应的条目index，只在可动态添加的问题项中有效。
     *
     * 为0的情况代表只是单项条目
     */
    var order: Int? = null
): IEntity()

@Suppress("unused")
enum class AnswerType(@JsonValue val value: String) {
    /// 由于`undefined`不是有效的JSON值，所以用`null`来代替`undefined`的作用（更新内容标记删除）
    NULL("null"),
    NUMBER("number"),
    BOOLEAN("boolean"),
    STRING("string"),
    ARRAY("array"),
    OBJECT("object")
}

/**
 * 一个问卷的完整回答内容（不一定全部填完）
 */
@Entity @Table
class Answer(
    @ManyToOne
    @JoinColumn(nullable = true)
    var creator: User? = null,

    /**
     * 所对应问卷的id
     */
    var schemaId: IdType = 0,

    /**
     * 回答的json结构的根节点id
     */
    var rootObjectId: IdType = 0
): ITimestampEntity()

@Repository
interface AnswerValueRepository: IQueryRepository<AnswerValue, IdType>

@Repository
interface AnswerRepository: IQueryRepository<Answer, IdType> {
    @Modifying
    @Query("update Answer a set a.creator = null where a.creator.id = :creatorId")
    fun markDeletedByCreator(creatorId: IdType): Int

    @Query("select a.id from Answer a where a.creator.id in :creatorList")
    fun findIdsWhenCreatorIn(creatorList: List<IdType>): List<IdType>
}
