package com.tjh.riskfactor.entity

import javax.persistence.*

@MappedSuperclass
open class IEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0
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

