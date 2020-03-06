package com.tjh.riskfactor.common

import javax.persistence.*

typealias IdType = Int

@MappedSuperclass
open class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: IdType = 0
) {
    override fun equals(other: Any?): Boolean {
        if(other == null) return false
        if(this === other) return true
        return (other as? BaseEntity)?.id == id
    }

    override fun hashCode(): Int = id.hashCode()
}

/**
 * 常用Projection：仅需要Id
 */
interface IdOnly {
    val id: IdType
}
