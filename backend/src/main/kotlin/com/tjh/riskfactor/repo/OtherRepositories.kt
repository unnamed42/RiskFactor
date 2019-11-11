package com.tjh.riskfactor.repo

import com.tjh.riskfactor.entity.SaveGuard
import com.tjh.riskfactor.entity.form.AnswerEntry
import com.tjh.riskfactor.entity.form.Question
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AnswerEntryRepository : JpaRepository<AnswerEntry, Int>

@Repository
interface QuestionRepository : JpaRepository<Question, Int>

@Repository
interface SaveGuardRepository : JpaRepository<SaveGuard, Int> {
    @Modifying
    @Query(value = "insert into save_guard(id) value (:id)", nativeQuery = true)
    fun insert(id: Int)
}
