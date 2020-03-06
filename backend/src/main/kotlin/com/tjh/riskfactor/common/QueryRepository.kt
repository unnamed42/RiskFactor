package com.tjh.riskfactor.common

import org.springframework.data.domain.Sort
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean

import com.tjh.riskfactor.common.notFound

import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection

import javax.persistence.EntityNotFoundException
import kotlin.reflect.KProperty

@NoRepositoryBean
interface QueryRepository<T, Id>:
    JpaRepository<T, Id>,
    JpaSpecificationExecutor<T>,
    JpaSpecificationExecutorWithProjection<T>

/**
 * Sort的辅助函数
 */
fun KProperty<*>.sorted(): Sort = Sort.by(Sort.Direction.ASC, this.name)

fun KProperty<*>.sortedDesc(): Sort = Sort.by(Sort.Direction.DESC, this.name)

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
 * 辅助扩展属性，用来获取Entity的名字
 */
inline val <reified E, R> R.entityName where R: JpaRepository<E, *>
    get() = E::class.java.simpleName

inline fun <reified E, T, Id, R> R.notFound(content: T) where R: JpaRepository<E, Id> =
    notFound(entityName, content.toString())

inline fun <reified E, Id, R: JpaRepository<E, Id>> R.mustHave(id: Id) =
    if(this.existsById(id)) true else throw this.notFound(id)

/**
 * 获取实体的“引用”，即使用延迟加载机制。当实体不存在时，访问属性会抛出异常[EntityNotFoundException]。
 *
 * 获取的实际上是hibernate提供的proxy object因此永远不会是null，同时也要注意，如果属性未获取时一定要在获取它的`@Transactional`中使用，
 * 否则会抛出[org.hibernate.LazyInitializationException]。
 *
 * 在只需要[update]的情况下，用这个方法，再set相应属性，以避免直接获取的[JpaRepository.findById]将所有属性都获取一遍。这个select过程本可以避免。
 */
fun <E, Id, R> R.findLazy(id: Id): E where R: JpaRepository<E, Id> = this.getOne(id)

/**
 * 提取实体的一个属性
 * @param id 实体的ID
 * @param accessor 属性提取器
 */
inline fun <Id, E, P, R> R.access(id: Id, accessor: (E) -> P): P? where R: JpaRepository<E, Id> =
    try { accessor(findLazy(id)) } catch (e: EntityNotFoundException) { null }

inline fun <reified E, Id, P, R> R.accessChecked(id: Id, accessor: (E) -> P): P where R: JpaRepository<E, Id> =
    this.access(id, accessor) ?: throw notFound(entityName, id.toString())

inline fun <reified E, Id, R> R.update(id: Id, properties: (E) -> Unit): E where R: JpaRepository<E, Id> =
    this.save(findLazy(id).also(properties))

inline fun <reified E, Id, R> R.updateChecked(id: Id, accessor: (E) -> Boolean): E where R: JpaRepository<E, Id> =
    try {
        findLazy(id).also{ if(accessor(it)) save(it) }
    } catch (e: EntityNotFoundException) { throw notFound(entityName, id.toString()) }

inline fun <reified E, Id, R> R.findChecked(id: Id): E where R: JpaRepository<E, Id> =
    findById(id).orElseThrow { notFound(entityName, id.toString()) }
