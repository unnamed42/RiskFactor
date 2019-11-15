package com.tjh.riskfactor.repo

import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository

import com.tjh.riskfactor.entity.form.Section
import com.tjh.riskfactor.entity.view.SectionView

@Repository
interface SectionRepository: JpaRepository<Section, Int> {

    @Query(nativeQuery = true, value =
        "select s.* from section s inner join task_sections ts " +
            "on s.id = ts.sid and ts.tid = :taskId " +
            "order by ts.seq"
    )
    fun findAllByOwnerTaskId(taskId: Int): List<Section>

    @Query("select s.id as id, s.title as title from Task t join t.sections s on t.id = :taskId")
    fun sectionViewsOfTask(taskId: Int): List<SectionView>

}
