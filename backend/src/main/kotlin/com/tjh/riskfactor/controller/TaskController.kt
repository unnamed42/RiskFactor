package com.tjh.riskfactor.controller

import com.fasterxml.jackson.annotation.JsonInclude

import org.springframework.web.bind.annotation.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.core.annotation.AuthenticationPrincipal

import com.tjh.riskfactor.repo.*
import com.tjh.riskfactor.entity.User
import com.tjh.riskfactor.entity.form.Answer
import com.tjh.riskfactor.entity.form.QuestionType
import com.tjh.riskfactor.entity.view.*
import com.tjh.riskfactor.security.JwtUserDetails
import com.tjh.riskfactor.util.fetchEager

@CrossOrigin
@RestController
class TaskController(
    private val repo: TaskRepository,
    private val users: UserRepository,
    private val groups: GroupRepository,
    private val questions: QuestionRepository,
    private val answers: AnswerRepository
) {

    /**
     * 创建或修改的回答。返回的格式为：
     * ```
     * {
     *      "id": [回答的id]
     * }
     * ```
     * @param answerId 为`null`时代表是要创建一个新的[Answer]，否则是修改id为[answerId]的[Answer]
     * @param taskId 项目id
     * @param creator 创建者（用户）实体
     * @param body 回答内容，应该是合法的JSON内容
     * @return 新创建的回答id
     */
    @Transactional
    fun postAnswer(answerId: Int?, taskId: Int, creator: User, body: String): Map<String, Int> {
        val answer = if(answerId == null)
            Answer(creator = creator, task = repo.findChecked(taskId)).apply { this.body = body }
        else
            answers.updateChecked(answerId) { it.body = body }
        return mapOf("id" to answers.save(answer).id)
    }

    /**
     * 描述Section（type为header的Question）的简略情况，即给出名称（label）
     * 如果其下属list还有Section，返回对象的list中也包含下属Section的简略情况
     * 如果不是Section，返回null
     * @param id Question的id
     */
    private fun describeSection(id: Int): SectionResponse? {
        val view = questions.questionView(id) ?: throw questions.notFound(id)
        if(view.type != QuestionType.HEADER)
            return null
        val ids = questions.listIdsOf(id)
        val ret = SectionResponse(view.label ?: "")
        return if(ids.isEmpty() || questions.typeOf(ids.first()) != QuestionType.HEADER)
            ret
        else
            ret.apply { list = ids.mapNotNull { describeSection(it) } }
    }

    class SectionResponse(
        val name: String
    ) {
        @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
        var list: List<SectionResponse> = emptyList()
    }

    /**
     * 获取全部项目的基础信息
     * @return 全部项目的基础信息
     */
    @GetMapping("/tasks")
    fun tasks() = repo.taskViews()

    /**
     * 获取某个项目的基础信息
     * @param id 项目id
     * @return 基础信息
     */
    @GetMapping("/tasks/{id}")
    fun task(@PathVariable id: Int) = repo.taskView(id) ?: throw repo.notFound(id)

    @GetMapping("/tasks/{id}/layout")
    fun layout(@PathVariable id: Int) = repo.accessChecked(id) { it.list.fetchEager() }

    @GetMapping("/tasks/{id}/sections/name")
    fun sectionNames(@PathVariable id: Int) =
        questions.listIdsOfTask(id).mapNotNull(this::describeSection)

    @GetMapping("/tasks/{id}/mtime")
    fun modifiedTime(@PathVariable id: Int) = repo.accessChecked(id) { it.mtime.time }

    @GetMapping("/tasks/{id}/answers")
    fun answers(@PathVariable id: Int, @AuthenticationPrincipal details: JwtUserDetails): List<AnswerView> {
        // 是root组，返回所有内容
        if(details.isRoot)
            return answers.findAllByTaskId(id)
        // 根据是否是组管理员，返回所有内容
        val gid = users.findManagedGroupId(details.id)
        return if(gid == null)
            answers.findTaskAnswersCreatedBy(id, details.id)
        else
            answers.findAllByTaskIdCreatedBy(id, groups.findMemberNames(gid))
    }

    @PostMapping("/tasks/{id}/answers")
    fun postAnswer(@PathVariable id: Int, @AuthenticationPrincipal details: JwtUserDetails,
                   @RequestBody body: String) =
        postAnswer(null, id, details.user, body)

    @PutMapping("/tasks/{id}/answers/{answerId}")
    fun updateAnswer(@PathVariable id: Int, @PathVariable answerId: Int,
                     @AuthenticationPrincipal details: JwtUserDetails, @RequestBody body: String) =
        postAnswer(answerId, id, details.user, body)

}
