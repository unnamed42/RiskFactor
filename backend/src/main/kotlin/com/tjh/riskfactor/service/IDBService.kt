package com.tjh.riskfactor.service

import org.springframework.data.jpa.repository.JpaRepository

import javax.persistence.EntityNotFoundException

import com.tjh.riskfactor.entity.ID
import com.tjh.riskfactor.entity.IEntity
import com.tjh.riskfactor.error.notFound

/**
 * Service的基类，将一些常用接口自[JpaRepository]成员[repo]暴露出来。Service层封装是为了避免Controller层直接使用[JpaRepository]，
 * 直接使用容易产生[org.springframework.transaction.annotation.Transactional]的一些问题。
 *
 * Checked系列接口是在非Checked（实体不存在时返回`null`]）的基础上，如果不存在则抛出[org.springframework.web.server.ResponseStatusException].
 * 该系列是为了方便Controller返回404错误使用，不要在Controller之外的地方使用。
 */
abstract class IDBService<T: IEntity>(@PublishedApi internal val entityName: String) {

    protected abstract val repo: JpaRepository<T, ID>

    /**
     * 删掉数据库表中的全部数据
     */
    fun drop() = repo.deleteAllInBatch()

    fun save(item: T): T = repo.save(item)
    fun saveAll(items: Iterable<T>): MutableList<T> = repo.saveAll(items)

    fun remove(id: Int) = repo.deleteById(id)
    fun has(id: Int) = repo.existsById(id)

    /**
     * 根据id查找相应实体。将Spring Data JPA返回的[java.util.Optional]转换成kotlin的nullable.
     */
    fun find(id: Int): T? = repo.findById(id).orElse(null)

    /**
     * 获取实体的“引用”，即使用延迟加载机制。当实体不存在时，访问属性会抛出异常[EntityNotFoundException]。
     *
     * 获取的实际上是hibernate提供的proxy object因此永远不会是null，同时也要注意，如果属性未获取时一定要在获取它的`@Transactional`中使用，
     * 否则会抛出[org.hibernate.LazyInitializationException]。
     *
     * 在只需要update的情况下，用这个方法，再set相应属性，以避免直接获取的[find]将所有属性都获取一遍。这个select过程
     * 本可以避免。
     */
    @PublishedApi
    internal fun findLazy(id: Int): T = repo.getOne(id)

    /**
     * 提取实体的一个属性
     * @param id 实体的ID
     * @param accessor 属性提取器
     */
    inline fun <P> access(id: Int, accessor: (T) -> P): P? =
        try { accessor(findLazy(id)) } catch (e: EntityNotFoundException) { null }

    inline fun <P> accessChecked(id: Int, accessor: (T) -> P): P =
        access(id, accessor) ?: throw notFound(entityName, id.toString())

    inline fun updateChecked(id: Int, accessor: (T) -> Unit): T =
        try { findLazy(id).also(accessor) } catch (e: EntityNotFoundException) { throw notFound(entityName, id.toString()) }

    fun findChecked(id: Int): T = repo.findById(id).orElseThrow { notFound(entityName, id.toString()) }

}
