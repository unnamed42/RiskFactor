package com.tjh.riskfactor.service

import com.tjh.riskfactor.error.notFound
import org.springframework.data.jpa.repository.JpaRepository

abstract class IDBService<T>(private val entityName: String) {

    protected abstract val repo: JpaRepository<T, Int>

    fun drop() = repo.deleteAllInBatch()

    fun save(item: T) = repo.save(item)
    fun saveAll(items: Iterable<T>): List<T> = repo.saveAll(items)

    fun remove(id: Int) = repo.deleteById(id)
    fun has(id: Int) = repo.existsById(id)
    fun find(id: Int): T? = repo.findById(id).orElse(null)
    fun checkedFind(id: Int): T = repo.findById(id).orElseThrow { notFound(entityName, id.toString()) }

    fun ensure(id: Int) {
        if(!has(id))
            throw notFound(entityName, id.toString())
    }

}
