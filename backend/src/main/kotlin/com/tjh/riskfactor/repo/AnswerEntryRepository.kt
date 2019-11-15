package com.tjh.riskfactor.repo

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.Modifying

import com.tjh.riskfactor.entity.form.AnswerEntry
import com.tjh.riskfactor.entity.view.AnswerEntryView

@Repository
interface AnswerEntryRepository: JpaRepository<AnswerEntry, Int> {

    @Query(nativeQuery = true, value =
        "select ae.question_id as qid, ae.value as value from answer_entry ae " +
            "inner join answer a on ae.answer_id = a.id and a.id = :answerId"
    )
    fun valueViewsOf(answerId: Int): List<AnswerEntryView>

    @Modifying
    @Query(nativeQuery = true, value =
        "insert into answer_entry(`value`, answer_id, question_id) value (:value, :answerId, :questionId) " +
            "on duplicate key update `value` = values(`value`)"
    )
    fun putValue(answerId: Int, questionId: Int, value: String)

}
