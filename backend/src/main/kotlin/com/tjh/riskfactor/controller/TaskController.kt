package com.tjh.riskfactor.controller

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal

import com.tjh.riskfactor.service.*
import com.tjh.riskfactor.entity.view.*
import com.tjh.riskfactor.error.notFound
import com.tjh.riskfactor.security.JwtUserDetails

@CrossOrigin
@RestController
class TaskController {

    @Autowired private lateinit var service: TaskService

    @Autowired private lateinit var users: UserService
    @Autowired private lateinit var answers: AnswerService
    @Autowired private lateinit var sections: SectionService

    /**
     * 获取全部项目的基础信息
     * @return 全部项目的基础信息
     */
    @GetMapping("/tasks")
    fun tasks() = service.tasks()

    /**
     * 获取某个项目的基础信息
     * @param id 项目id
     * @return 基础信息
     */
    @GetMapping("/tasks/{id}")
    fun task(@PathVariable id: Int) = service.taskBrief(id) ?: throw notFound("task", id.toString())

    @GetMapping("/tasks/{id}/sections")
    fun sections(@PathVariable id: Int) = sections.sectionsOfTask(id)

    @GetMapping("/tasks/{id}/sections/name")
    fun sectionNames(@PathVariable id: Int) = service.taskSectionsInfo(id)

    @GetMapping("/tasks/{id}/answers")
    fun answers(@PathVariable id: Int, @AuthenticationPrincipal details: JwtUserDetails): List<AnswerView> {
        // 是root组，返回所有内容
        if(details.isRoot)
            return service.taskAnswers(id)
        // 根据是否是组管理员，返回所有内容
        val gid = users.managedGroupId(details.id)
        return if(gid == null) service.userAnswers(id, details.id) else service.centerAnswers(id, gid)
    }

    @PostMapping(value = ["/tasks/{id}/answers/file"], consumes = ["application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"])
    fun importAnswer(@PathVariable id: Int, @AuthenticationPrincipal details: JwtUserDetails,
                     @RequestParam("file") file: MultipartFile) {
        answers.importExcel(id, details.id, file.inputStream)
    }

}
