package com.tjh.riskfactor.repository

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.Modifying

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
interface SchemaRepository: IQueryRepository<Schema, IdType> {
    @Query("select s.id from Schema s where s.creatorId = :creatorId")
    fun findByCreatorId(creatorId: IdType): List<IdType>

    @Modifying
    @Query("""update Schema s set s.creatorId = 0, s.groupId = 0
        where s.creatorId = :creatorId
    """)
    fun markDeletedByCreator(creatorId: IdType): Int
}
