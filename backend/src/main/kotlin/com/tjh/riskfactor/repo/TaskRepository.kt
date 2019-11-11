package com.tjh.riskfactor.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

import com.tjh.riskfactor.entity.form.Task
import com.tjh.riskfactor.entity.view.SectionView
import com.tjh.riskfactor.entity.view.TaskView

private const val TASK_BRIEF_ALL = "select t.id as id, t.name as name, t.mtime as mtime, g.displayName as center from Task t join t.group g "

@Repository
interface TaskRepository : JpaRepository<Task, Int> {

    @Query(TASK_BRIEF_ALL + "on t.id = :id")
    fun findTaskInfoById(id: Int): TaskView?

    @Query(TASK_BRIEF_ALL)
    fun findAllTasks(): List<TaskView>

    @Query("select s.id as id, s.title as title from Task t join t.sections s on t.id = :id")
    fun findSectionNamesById(id: Int): List<SectionView>

}
