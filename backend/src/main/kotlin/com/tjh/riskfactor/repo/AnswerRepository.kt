package com.tjh.riskfactor.repo

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.JpaRepository

import com.tjh.riskfactor.entity.form.Answer
import com.tjh.riskfactor.entity.view.AnswerView

private const val ANSWER_BRIEF = "select a.id as id, c.username as creator, a.mtime as mtime from Answer a inner join a.task t on t.id = :taskId "

@Repository
interface AnswerRepository: JpaRepository<Answer, Int> {

    @Query("select body from Answer where id = :id")
    fun bodyOf(id: Int): String?

    @Query(ANSWER_BRIEF + "inner join a.creator c on c.id = :uid")
    fun findTaskAnswersCreatedBy(taskId: Int, uid: Int): List<AnswerView>

    @Query(ANSWER_BRIEF + "inner join a.creator c")
    fun findAllByTaskId(taskId: Int): List<AnswerView>

    @Query(ANSWER_BRIEF + "inner join a.creator c on c.username in :usernames")
    fun findAllByTaskIdCreatedBy(taskId: Int, usernames: Collection<String>): List<AnswerView>

}
