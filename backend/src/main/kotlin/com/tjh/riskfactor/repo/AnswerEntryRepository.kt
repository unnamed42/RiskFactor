package com.tjh.riskfactor.repo

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

import com.tjh.riskfactor.entity.form.AnswerEntry
import com.tjh.riskfactor.entity.view.AnswerEntryView

@Repository
interface AnswerEntryRepository: JpaRepository<AnswerEntry, Int> {

    @Query(nativeQuery = true, value =
        "select ae.question_id as qid, ae.value as value from answer_entry ae " +
            "inner join answer a on ae.answer_id = a.id and a.id = :answerId"
    )
    fun valuesOf(answerId: Int): List<AnswerEntryView>

    @Query(nativeQuery = true, value =
        "select ae.* from answer_entry ae inner join answer a on ae.answer_id = a.id " +
            "and a.id = :answerId and ae.question_id in :questionIds"
    )
    fun entriesOf(answerId: Int, questionIds: Collection<Int>): List<AnswerEntry>

}
