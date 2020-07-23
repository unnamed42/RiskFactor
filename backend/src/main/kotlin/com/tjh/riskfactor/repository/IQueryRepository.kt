package com.tjh.riskfactor.repository

import org.springframework.data.domain.Sort
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean

import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection

import javax.persistence.EntityNotFoundException

import kotlin.reflect.KProperty

@NoRepositoryBean
interface IQueryRepository<T, Id>:
    JpaRepository<T, Id>,
    JpaSpecificationExecutor<T>,
    JpaSpecificationExecutorWithProjection<T>

/**
 * Sort的辅助函数
 */
fun KProperty<*>.sorted(): Sort = Sort.by(Sort.Direction.ASC, this.name)

fun KProperty<*>.sortedDesc(): Sort = Sort.by(Sort.Direction.DESC, this.name)

fun KProperty<String>.sortedByLength(): Sort = Sort.by("LENGTH($name)")

/**
 * 以下扩展是为了不使用[Pageable]
 */

fun <T> `true`() = Specification<T> { _, _, builder -> builder.conjunction() }

fun <T> `false`() = Specification<T> { _, _, builder -> builder.disjunction() }

fun <E, R> R.exists(spec: Specification<E>): Boolean where R: JpaSpecificationExecutor<E> =
    this.count(spec) != 0L

inline fun <reified P, E, R> R.findAllProjected(): List<P> where R: JpaSpecificationExecutorWithProjection<E> =
    this.findAllProjected(`true`())

inline fun <reified P, E, R> R.findOneProjected(spec: Specification<E>): P? where R: JpaSpecificationExecutorWithProjection<E> =
    this.findOne(spec, P::class.java).orElse(null)

inline fun <reified P, E, R> R.findAllProjected(spec: Specification<E>): List<P> where R: JpaSpecificationExecutorWithProjection<E> =
    this.findAll(spec, P::class.java, Pageable.unpaged()).content

inline fun <reified P, E, R> R.findAllProjected(spec: Specification<E>, sort: Sort): List<P> where R: JpaSpecificationExecutorWithProjection<E> =
    this.findAll(spec, P::class.java, PageRequest.of(0, Int.MAX_VALUE, sort)).content

/**
 * [JpaRepository]扩展
 */

inline fun <reified E, T, Id, R> R.notFound(content: T) where R: JpaRepository<E, Id> =
    com.tjh.riskfactor.common.notFound(E::class.java.simpleName, content.toString())

/**
 * 获取实体的“引用”，即使用延迟加载机制。当实体不存在时，访问属性会抛出异常[EntityNotFoundException]。
 *
 * 获取的实际上是hibernate提供的proxy object因此永远不会是null，同时也要注意，如果属性未获取时一定要在获取它的`@Transactional`中使用，
 * 否则会抛出[org.hibernate.LazyInitializationException]。
 *
 * 在更新数据且不需要原数据的情况下，用这个方法，再set相应属性，以避免直接获取的[JpaRepository.findById]将所有属性都获取一遍。这个select过程本可以避免。
 */
fun <E, Id, R> R.findLazy(id: Id): E where R: JpaRepository<E, Id> =
    this.getOne(id)

/**
 * 提取实体的一个属性。如果[id]对应实体不存在，不抛出异常而是返回`null`。
 * @param id 实体的ID
 * @param property 属性提取器
 */
inline fun <Id, E, P, R> R.propertyOfUnchecked(id: Id, property: E.() -> P): P? where R: JpaRepository<E, Id> =
    try { findLazy(id).property() } catch (e: EntityNotFoundException) { null }

inline fun <reified E, Id, P, R> R.propertyOf(id: Id, property: E.() -> P): P where R: JpaRepository<E, Id> =
    this.propertyOfUnchecked(id, property) ?: throw notFound(id)

/**
 * 更新实体的属性
 * @param accessor 更新函数。如果返回为true，那么代表应该更新，此时存入数据库；否则不更新
 */
inline fun <reified E, Id, R> R.updateIf(id: Id, accessor: (E) -> Boolean): E where R: JpaRepository<E, Id> =
    try { findLazy(id).also { if(accessor(it)) save(it) } } catch (e: EntityNotFoundException) { throw notFound(id) }

inline fun <reified E, Id, R> R.update(id: Id, accessor: E.() -> Unit): E where R: JpaRepository<E, Id> =
    updateIf(id) { it.accessor(); true }

inline fun <reified E, Id, R> R.find(id: Id): E where R: JpaRepository<E, Id> =
    findById(id).orElseThrow { notFound(id) }
