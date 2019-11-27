package com.tjh.riskfactor.service;

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.repo.*
import com.tjh.riskfactor.entity.form.*
import com.tjh.riskfactor.error.notFound
import com.tjh.riskfactor.util.fetchEager

@Service
class TaskService(
    private val questions: QuestionService,
    override val repo: TaskRepository
): IDBService<Task>("task") {

    fun taskBrief(id: Int) = repo.taskView(id) ?: throw notFound("task", id.toString())

    fun taskViews() = repo.taskViews()

    /**
     * 获取项目的问题结构和内容
     * @param taskId 项目id
     */
    @Transactional
    fun taskLayout(taskId: Int) = accessChecked(taskId) { it.list.fetchEager() }

    /**
     * 获取项目下属的分节的基本信息
     * @param id 项目id
     * @return 所有分节的基本信息
     */
    fun taskSectionsInfo(id: Int) =
        questions.listIdsOfTask(id).mapNotNull(questions::describeSection)

}
