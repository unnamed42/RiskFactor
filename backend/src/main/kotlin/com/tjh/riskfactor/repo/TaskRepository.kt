package com.tjh.riskfactor.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

import com.tjh.riskfactor.entity.form.Task
import com.tjh.riskfactor.entity.view.*

private const val TASK_BRIEF_ALL = "select t.id as id, t.name as name, t.mtime as mtime, g.displayName as center from Task t join t.group g "

@Repository
interface TaskRepository : JpaRepository<Task, Int> {

    @Query("select t.mtime as mtime from Task t where t.id = :id")
    fun mtime(id: Int): MtimeView?

    @Query(TASK_BRIEF_ALL + "on t.id = :id")
    fun taskView(id: Int): TaskView?

    @Query(TASK_BRIEF_ALL)
    fun taskViews(): List<TaskView>

}
