package com.tjh.riskfactor.repo

import javax.persistence.EntityNotFoundException

import org.springframework.data.jpa.repository.JpaRepository

import com.tjh.riskfactor.error.notFound
import com.tjh.riskfactor.entity.ID

/**
 * 删掉数据库表中的全部数据
 */
fun <R: JpaRepository<*, *>> R.drop() = deleteAllInBatch()

/**
 * 辅助扩展属性，用来获取Entity的名字
 */
inline val <reified E, R: JpaRepository<E, *>> R.entityName: String
    get() = E::class.java.simpleName

inline fun <reified E, R: JpaRepository<E, *>> R.notFound(content: Any) =
    notFound(entityName, content.toString())

/**
 * 根据id查找相应实体。将Spring Data JPA返回的[java.util.Optional]转换成kotlin的nullable.
 */
fun <E, R: JpaRepository<E, ID>> R.find(id: ID): E? = this.findById(id).orElse(null)

/**
 * 获取实体的“引用”，即使用延迟加载机制。当实体不存在时，访问属性会抛出异常[EntityNotFoundException]。
 *
 * 获取的实际上是hibernate提供的proxy object因此永远不会是null，同时也要注意，如果属性未获取时一定要在获取它的`@Transactional`中使用，
 * 否则会抛出[org.hibernate.LazyInitializationException]。
 *
 * 在只需要update的情况下，用这个方法，再set相应属性，以避免直接获取的[find]将所有属性都获取一遍。这个select过程本可以避免。
 */
fun <E, R: JpaRepository<E, ID>> R.findLazy(id: ID) = this.getOne(id)

/**
 * 提取实体的一个属性
 * @param id 实体的ID
 * @param accessor 属性提取器
 */
inline fun <E, P, R: JpaRepository<E, ID>> R.access(id: ID, accessor: (E) -> P): P? =
    try { accessor(findLazy(id)) } catch (e: EntityNotFoundException) { null }

inline fun <reified E, P, R: JpaRepository<E, ID>> R.accessChecked(id: ID, accessor: (E) -> P): P =
    access(id, accessor) ?: throw notFound(entityName, id.toString())

inline fun <reified E, R: JpaRepository<E, ID>> R.updateChecked(id: ID, accessor: (E) -> Unit): E =
    try { findLazy(id).also(accessor) } catch (e: EntityNotFoundException) { throw notFound(entityName, id.toString()) }

inline fun <reified E, R: JpaRepository<E, ID>> R.findChecked(id: ID): E =
    findById(id).orElseThrow { notFound(entityName, id.toString()) }
