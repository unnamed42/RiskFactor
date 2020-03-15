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

}

private inline fun <reified T: Any> T.dropAll() = T::class.declaredMemberProperties
    .filter { it.returnType.jvmErasure.isSubclassOf(JpaRepository::class) }
    .map { it.get(this) as JpaRepository<*, *> }.forEach { it.deleteAllInBatch() }
