package com.tjh.riskfactor.service;

import org.springframework.stereotype.Service;

import com.tjh.riskfactor.repo.*;
import com.tjh.riskfactor.entity.form.*;
import org.springframework.beans.factory.annotation.Autowired

@Service
class TaskService: IDBService<Task>("task") {

    @Autowired private lateinit var questions: QuestionRepository
    @Autowired private lateinit var sections: SectionRepository
    @Autowired private lateinit var answers: AnswerRepository
    @Autowired private lateinit var groups: GroupService

    @Autowired override lateinit var repo: TaskRepository

    fun taskBrief(id: Int) = repo.findTaskInfoById(id)
    fun tasks() = repo.findAllTasks()

    /**
     * 获取项目下属的分节的基本信息
     * @param id 项目id
     * @return 所有分节的基本信息
     */
    fun taskSectionsInfo(id: Int) = repo.findSectionNamesById(id)

    /**
     * 获取项目下属的所有回答
     * @param taskId 项目id
     * @return 所有回答
     */
    fun taskAnswers(taskId: Int) = answers.findAllByTaskId(taskId)

    /**
     * 获取项目下属的所有由某用户创建的回答
     * @param taskId 项目id
     * @param uid 用户id
     * @return 所有该用户创建的回答
     */
    fun userAnswers(taskId: Int, uid: Int) = answers.findTaskAnswersCreatedBy(taskId, uid)

    /**
     * 获取项目下属所有由某用户组创建的回答
     * @param taskId 项目id
     * @param gid 用户组id
     * @return 所有该用户组创建的回答
     */
    fun centerAnswers(taskId: Int, gid: Int) = answers.findAllByTaskIdCreatedBy(taskId, groups.memberNames(gid))

    internal fun saveSections(sections: Iterable<Section>) = this.sections.saveAll(sections)
    internal fun saveQuestions(questions: Iterable<Question>) = this.questions.saveAll(questions)

}
