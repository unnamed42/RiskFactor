package com.tjh.riskfactor.service;

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import com.tjh.riskfactor.entity.User
import com.tjh.riskfactor.repo.*
import com.tjh.riskfactor.entity.form.*

@Service
class TaskService(
    private val questions: QuestionService,
    private val sections: SectionService,
    private val answers: AnswerService,
    override val repo: TaskRepository
): IDBService<Task>("task") {

    fun taskView(id: Int) = repo.taskView(id)
    fun taskViews() = repo.taskViews()

    fun modifiedTime(id: Int) = repo.mtime(id)

    /**
     * 获取项目下属的分节的基本信息
     * @param id 项目id
     * @return 所有分节的基本信息
     */
    fun taskSectionsInfo(id: Int) = sections.sectionViewsOfTask(id)

    /**
     * 创建新的回答
     * @param taskId 项目id
     * @param creator 创建者（用户）实体
     * @param body 回答内容
     * @return 新创建的回答id
     */
    @Transactional
    fun createAnswer(taskId: Int, creator: User, body: Map<String, Any>): Map<String, Int> {
        val answer = answers.save(Answer(creator = creator, task = checkedFind(taskId)))
        val entries = body.entries.map { (k, v) ->
            val question = questions.checkedFind(k.toInt())
            AnswerEntry(answer = answer, question = question,
                value = v.toString())
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
