package com.tjh.riskfactor.repository

import org.springframework.stereotype.Repository

import javax.persistence.*

/**
 * 代表一个完整的问卷结构
 */
@Entity @Table
class Schema(
    /**
     * 创建者的用户id
     */
    var creatorId: IdType = 0,

    /**
     * 所有组的id，不一定是创建者的用户组
     */
    var groupId: IdType = 0,

    @Column(nullable = false)
    var name: String = "",

    /**
     * 根问题的id
     */
    var rootObjectId: IdType = 0
): ITimestampEntity()

@Repository
interface SchemaRepository: IQueryRepository<Schema, IdType>
