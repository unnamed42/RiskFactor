package com.tjh.riskfactor.repository

import javax.persistence.*

typealias IdType = Int

typealias EpochTime = Long

@MappedSuperclass
open class IEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: IdType = 0
) {

    override fun equals(other: Any?): Boolean =
        this === other || (other as? IEntity)?.id == id

    override fun hashCode() = id.hashCode()
}

@MappedSuperclass
open class ITimestampEntity(
    var createdAt: EpochTime = System.currentTimeMillis(),
    var modifiedAt: EpochTime = System.currentTimeMillis()
): IEntity()

/**
 * 常用Projection：仅需要Id
 */
interface IdOnly {
    val id: IdType
}
