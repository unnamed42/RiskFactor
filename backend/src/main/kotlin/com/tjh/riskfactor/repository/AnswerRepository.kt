package com.tjh.riskfactor.repository

import org.springframework.stereotype.Repository

import javax.persistence.*

/**
 * 单个问题的回答内容
 */
@Entity @Table(uniqueConstraints = [
    UniqueConstraint(columnNames = ["question_id", "order"])])
class AnswerValue(
    /**
     * 所属回答集合的id
     */
    var tableId: IdType = 0,

    /**
     * 对应问题的id
     */
    @Column(name = "question_id")
    var questionId: IdType = 0,

    /**
     * 所对应的条目index（从1开始），只在可动态添加的问题项中有效。
     *
     * 为0的情况代表只是单项条目
     */
    @Column(name = "order")
    var order: Int = 0,

    /**
     * 内容
     */
    var value: String = ""
): IEntity()

/**
 * 一个问卷的完整回答内容（不一定全部填完）
 */
@Entity @Table
class Answer(
    /**
     * 创建者的用户id
     */
    var creatorId: IdType = 0,

    /**
     * 所属组的id
     */
    var groupId: IdType = 0,

    /**
     * 所对应问卷的id
     */
    var schemaId: IdType = 0,

    var createdAt: EpochTime = System.currentTimeMillis(),

    var modifiedAt: EpochTime = System.currentTimeMillis(),

    /**
     * 如果为true，则表示该回答集的内容为各问题的初始值
     */
    var isInitialData: Boolean = false
): IEntity()

@Repository
interface AnswerValueRepository: IQueryRepository<AnswerValue, IdType>

@Repository
interface AnswerRepository: IQueryRepository<Answer, IdType>
