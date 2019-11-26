package com.tjh.riskfactor.service

import org.springframework.data.jpa.repository.JpaRepository

import javax.persistence.EntityNotFoundException

import com.tjh.riskfactor.entity.IEntity
import com.tjh.riskfactor.error.notFound

abstract class IDBService<T: IEntity>(@PublishedApi internal val entityName: String) {

    protected abstract val repo: JpaRepository<T, Int>

    fun drop() = repo.deleteAllInBatch()

    fun save(item: T): T = repo.save(item)
    fun saveAll(items: Iterable<T>): MutableList<T> = repo.saveAll(items)

    fun remove(id: Int) = repo.deleteById(id)
    fun has(id: Int) = repo.existsById(id)

    fun find(id: Int): T? = repo.findById(id).orElse(null)

    /**
     * 获取实体的“引用”，即使用延迟加载机制。当实体不存在时，访问属性会抛出异常{@link EntityNotFoundException}。
     * 获取的实际上是hibernate提供的proxy object因此永远不会是null，同时也要注意一定要在获取它的@Transactional中使用，否则
     * 会抛出{@link LazyInitializationException}。
     */
    @PublishedApi
    internal fun findLazy(id: Int): T = repo.getOne(id)

    inline fun <P> access(id: Int, accessor: (T) -> P): P? =
        try { accessor(findLazy(id)) } catch (e: EntityNotFoundException) { null }

    inline fun <P> accessChecked(id: Int, accessor: (T) -> P): P =
        access(id, accessor) ?: throw notFound(entityName, id.toString())

    inline fun updateChecked(id: Int, accessor: (T) -> Unit): T =
        try { findLazy(id).also(accessor) } catch (e: EntityNotFoundException) { throw notFound(entityName, id.toString()) }

    fun findChecked(id: Int): T = repo.findById(id).orElseThrow { notFound(entityName, id.toString()) }

    fun ensure(id: Int) {
        if(!has(id))
            throw notFound(entityName, id.toString())
    }

}
