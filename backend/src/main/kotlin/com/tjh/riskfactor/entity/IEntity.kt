package com.tjh.riskfactor.entity

import javax.persistence.*

typealias ID = Int

@MappedSuperclass
open class IEntity(
    @get:Id @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: ID = 0
) {
    override fun equals(other: Any?): Boolean {
        if(other == null) return false
        if(this === other) return true
        return (other as? IEntity)?.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
