package com.tjh.riskfactor.controller

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.security.core.annotation.AuthenticationPrincipal

import com.tjh.riskfactor.service.*
import com.tjh.riskfactor.entity.view.*
import com.tjh.riskfactor.error.notFound
import com.tjh.riskfactor.security.JwtUserDetails

@CrossOrigin
@RestController
class TaskController(
    private val service: TaskService,
    private val users: UserService,
    private val answers: AnswerService,
    private val sections: SectionService
) {

    /**
     * 获取全部项目的基础信息
     * @return 全部项目的基础信息
     */
    @GetMapping("/tasks")
    fun tasks() = service.taskViews()

    /**
     * 获取某个项目的基础信息
     * @param id 项目id
     * @return 基础信息
     */
    @GetMapping("/tasks/{id}")
    fun task(@PathVariable id: Int) = service.taskView(id) ?: throw notFound("task", id.toString())

    @GetMapping("/tasks/{id}/sections")
    fun sections(@PathVariable id: Int) = sections.sectionsOfTask(id)

    @GetMapping("/tasks/{id}/sections/name")
    fun sectionNames(@PathVariable id: Int) = service.taskSectionsInfo(id)

    @GetMapping("/tasks/{id}/mtime")
    fun modifiedTime(@PathVariable id: Int) = service.modifiedTime(id)

    @GetMapping("/tasks/{id}/answers")
    fun answers(@PathVariable id: Int, @AuthenticationPrincipal details: JwtUserDetails): List<AnswerView> {
        // 是root组，返回所有内容
        if(details.isRoot)
            return answers.answersOfTask(id)
        // 根据是否是组管理员，返回所有内容
        val gid = users.managedGroupId(details.id)
        return if(gid == null) answers.answersCreatedBy(id, details.id) else answers.answersOfTask(id, gid)
    }

    @PostMapping("/tasks/{id}/answers")
    fun postAnswer(@PathVariable id: Int, @AuthenticationPrincipal details: JwtUserDetails,
                   @RequestBody body: Map<String, Any>) =
        service.createAnswer(id, details.user, body)

    @PostMapping(value = ["/tasks/{id}/answers/file"], consumes = ["application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"])
    fun importAnswer(@PathVariable id: Int, @AuthenticationPrincipal details: JwtUserDetails,
                     @RequestParam("file") file: MultipartFile) {
        val task = service.checkedFind(id)
        answers.importExcel(task, details.user, file.inputStream)
    }

}
