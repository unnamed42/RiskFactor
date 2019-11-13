package com.tjh.riskfactor.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.entity.User
import com.tjh.riskfactor.repo.*;
import com.tjh.riskfactor.entity.form.*;
import org.springframework.http.ResponseEntity

@Service
class TaskService: IDBService<Task>("task") {

    @Autowired private lateinit var questions: QuestionRepository
    @Autowired private lateinit var sections: SectionRepository
    @Autowired private lateinit var answers: AnswerService

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
     * 创建新的回答
     * @param taskId 项目id
     * @param creator 创建者（用户）实体
     * @param body 回答内容
     * @return 新创建的回答id
     */
    @Transactional
    fun createAnswer(taskId: Int, creator: User, body: Map<String, Any>): Map<String, Int> {
        val answer = answers.save(Answer().apply {
            this.creator = creator; this.task = checkedFind(taskId)
        })
        val entries = body.entries.map { (k, v) ->
            AnswerEntry(v.toString()).apply {
                // TODO: 弄个合理的错误处理
                this.answer = answer; this.question = questions.findById(k.toInt()).orElseThrow()
            }
        }
        answers.saveEntries(entries)
        return mapOf("id" to answer.id)
    }

    @Transactional
    fun exportAnswer(taskId: Int, answerId: Int) /*: ResponseEntity<ByteArray>*/ {
    }

    internal fun saveSections(sections: Iterable<Section>) = this.sections.saveAll(sections)
    internal fun saveQuestions(questions: Iterable<Question>) = this.questions.saveAll(questions)

}
