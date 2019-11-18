package com.tjh.riskfactor.repo

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.JpaRepository

import com.tjh.riskfactor.entity.form.Question
import com.tjh.riskfactor.entity.form.QuestionType
import com.tjh.riskfactor.entity.view.QuestionView

@Repository
interface QuestionRepository: JpaRepository<Question, Int> {

    @Query("select type as type, label as label from Question where id = :id")
    fun questionView(id: Int): QuestionView?

    @Query("select type as type from Question where id = :id")
    fun typeOf(id: Int): QuestionType?

    @Query(nativeQuery = true, value =
        "select qid from question_list where head = :id order by sequence"
    )
    fun listIdsOf(id: Int): List<Int>

    @Query(nativeQuery = true, value =
        "select qid from task_sections where tid = :taskId order by seq"
    )
    fun listIdsOfTask(taskId: Int): List<Int>

}
