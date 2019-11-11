package com.tjh.riskfactor.service

import com.tjh.riskfactor.error.ResponseErrors.notFound
import org.springframework.data.jpa.repository.JpaRepository

interface IDBService<T> {

    val entityName: String

    fun getRepo(): JpaRepository<T, Int>

    fun drop() = getRepo().deleteAllInBatch()

    fun save(item: T) = getRepo().save(item)
    fun saveAll(items: Iterable<T>): List<T> = getRepo().saveAll(items)

    fun remove(id: Int) = getRepo().deleteById(id)
    fun has(id: Int) = getRepo().existsById(id)
    fun find(id: Int): T? = getRepo().findById(id).orElse(null)
    fun checkedFind(id: Int): T = getRepo().findById(id).orElseThrow { notFound(entityName, id.toString()) }

    fun ensure(id: Int) {
        if(!has(id))
            throw notFound(entityName, id.toString())
    }

}
