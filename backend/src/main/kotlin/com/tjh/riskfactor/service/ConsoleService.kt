package com.tjh.riskfactor.service

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

import javax.persistence.*

@Entity @Table(name = "globals")
data class Globals(
    @Id val key: String = "",

    @Column(nullable = false)
    val value: String = ""
)

@Repository
interface GlobalsRepository: JpaRepository<Globals, String>

/**
 * 负责在[GlobalsRepository]的数据库表中插入flag。应该取个更贴切的名字
 */
@Service
class ConsoleService(
    private val globals: GlobalsRepository,
    private val accounts: AccountService,
    private val questions: SchemaService,
    private val answers: AnswerService
) {

    operator fun get(key: String): String? =
        globals.findById(key).map { it.value }.orElse(null)

    operator fun set(key: String, value: String) {
        globals.saveAndFlush(Globals(key, value))
    }

    fun dropAll() {
        accounts.dropAll()
        questions.dropAll()
        answers.dropAll()
    }

    /**
     * 反射获取Service层的所有[JpaRepository]成员，并逐个调用[JpaRepository.deleteAllInBatch]
     */
    private inline fun <reified Service: Any> Service.dropAll() = Service::class.declaredMemberProperties.asSequence()
        .filter { it.returnType.jvmErasure.isSubclassOf(JpaRepository::class) }
        .map { it.get(this) as JpaRepository<*, *> }.forEach { it.deleteAllInBatch() }

}
